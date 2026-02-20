import Dialog from "@mui/material/Dialog";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import { useEffect, useState } from "react";
import CsvPreviewTable from "./CsvPreviewTable";
import CsvUploadStep from "./CsvUploadStep";
import { useAuth } from "../auth/auth.context";
import { useWorkspaceContext } from "../contexts/workspace/workspace.context";
import CsvScanResultEditor from "./CsvScanResultEditor";
import { Box, CircularProgress, Grid } from "@mui/material";

type Step = "select" | "scanning" | "confirm";

export type ColumnOverride = {
    columnName: string,
    dataType: string,
    nullable: boolean,
    description: string
}

export interface CsvScanResponse {
    suggestedTableName: string,
    columns: {
        columnName: string,
        suggestedDataType: string,
        nullable: boolean
    }[];
}

export function ImportCsvModal({ open, onClose }: { open: boolean, onClose: () => void}) {
    const [uploadSessionId, setUploadSessionId] = useState<string | null>(null);
    const [file, setFile] = useState<File | null>(null);
    const [preview, setPreview] = useState<string[][]>([]);
    const [scan, setScan] = useState<CsvScanResponse | null>(null);
    const [step, setStep] = useState<Step>("select");
    const { token } = useAuth();
    const { currentWorkspaceId } = useWorkspaceContext();

    useEffect(() => {
        if (!file || step !== "scanning") return;

        const runScan = async() => {
            const form = new FormData();
            form.append("file", file);

            const res = await fetch("http://172.30.107.109:8080/api/tables/import/scan", {
                method: "POST",
                headers: {
                    Authorization: `Bearer ${token}`,
                    "X-Workspace-Id": `${currentWorkspaceId}`
                },
                body: form
            })

            const scan = await res.json();
            setScan(scan);
            console.log(scan)
            setStep("confirm");
        }

        runScan();
    }, [file, step])

    const handleSubmitConfirmTableRequest = async (payload: {
        tableName: string,
        description: string,
        primaryKeys: string[],
        columns: ColumnOverride[];
    }) => {
        const response = await fetch("http://172.30.107.109:8080/api/tables/import/confirm", {
                method: "POST",
                headers: {
                    Authorization: `Bearer ${token}`,
                    "X-Workspace-Id": `${currentWorkspaceId}`,
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    scan: scan, 
                    confirm: payload
                })
            }
        );

        const responseData = response.json()

        console.log(responseData);
    }

    return (
        <Dialog open={open} onClose={onClose} fullWidth maxWidth="xl">
            <DialogTitle>
                Import your data through CSV uploads.
            </DialogTitle>
            <DialogContent>
                {step === "select" ?
                <CsvUploadStep
                    previewRows={20}
                    onFileSelected={async (f, p) => {
                        setPreview(p);
                        setFile(f);
                        const response = await fetch("http://172.30.107.109:8080/api/tables/import/init", {
                            method: "POST",
                            headers: {
                                Authorization: `Bearer ${token}`,
                                "Content-Type": "application/json",
                                "X-Workspace-Id": `${currentWorkspaceId}`
                            },
                            body: JSON.stringify({
                                originalFileName: `${f.name}`
                            })
                        })
                        const responseData = await response.json();

                        const uploadSessionId = responseData.uploadSessionId;
                        setUploadSessionId(uploadSessionId);

                        setStep("scanning");

                        const uploadURL = responseData.uploadURL;
                        const uploadResponse = await fetch(uploadURL, {
                            method: "PUT",
                            headers: { 
                                "Content-Type": "text/csv",
                                "x-amz-meta-uploadsessionid": `${uploadSessionId}`
                            },
                            body: f
                        })

                        if (!uploadResponse.ok) {
                            throw new Error("Upload failed");
                        }
                        console.log("Upload successful");
                    }}
                />
                : 
                // <span> Confirm the data schema </span>
                <Grid container>
                    <Grid size={6}> 
                        {scan ?
                        <CsvScanResultEditor
                            scanResponse={scan}
                            handleSubmit={(pl) => 
                                // console.log(pl)
                                handleSubmitConfirmTableRequest(pl)
                            }
                        /> : <Box
                            sx={{
                                height: "100%",
                                minHeight: 400,
                                display: "flex",
                                alignItems: "center",
                                justifyContent: "center"
                            }}
                            >
                                <CircularProgress />
                            </Box>
                        }
                    </Grid>
                    <Grid size={6}> 
                        <CsvPreviewTable 
                            data={preview}
                        />
                    </Grid>
                </Grid>
                }
            </DialogContent>
        </Dialog>
    )
}