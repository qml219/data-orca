import Dialog from "@mui/material/Dialog";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import { useState } from "react";
import CsvPreviewTable from "./CsvPreviewTable";
import CsvUploadStep from "./CsvUploadStep";
import { useAuth } from "../auth/auth.context";
import { useWorkspaceContext } from "../contexts/workspace/workspace.context";
import CsvScanResultEditor from "./CsvScanResultEditor";
import { Box, CircularProgress, Grid } from "@mui/material";

type Step = "select" | "scanning" | "confirm" | "error";

export interface CsvScanResponse {
    suggestedTableName: string,
    columns: {
        columnName: string,
        suggestedDataType: string,
        nullable: boolean
    }[];
}

export function ImportCsvModal({ open, onClose }: { open: boolean, onClose: () => void }) {

    const [uploadSessionId, setUploadSessionId] = useState<string | null>(null);
    const [preview, setPreview] = useState<string[][]>([]);
    const [scan, setScan] = useState<CsvScanResponse | null>(null);
    const [step, setStep] = useState<Step>("select");

    const { token } = useAuth();
    const { currentWorkspaceId } = useWorkspaceContext();

    const handleSubmitConfirmTableRequest = async (payload: any) => {
        await fetch("http://172.30.107.109:8080/api/tables/import/confirm", {
            method: "POST",
            headers: {
                Authorization: `Bearer ${token}`,
                "X-Workspace-Id": `${currentWorkspaceId}`,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                uploadSessionId,
                confirm: payload
            })
        });
    };

    return (
        <Dialog open={open} onClose={onClose} fullWidth maxWidth="xl">
            <DialogTitle>
                Import your data through CSV uploads.
            </DialogTitle>

            <DialogContent>
                {step === "select" ? (
                    <CsvUploadStep
                        previewRows={20}
                        onFileSelected={async (f, p) => {
                            try {
                                setPreview(p);

                                // 1️⃣ INIT
                                const initRes = await fetch(
                                    "http://172.30.107.109:8080/api/tables/import/init",
                                    {
                                        method: "POST",
                                        headers: {
                                            Authorization: `Bearer ${token}`,
                                            "Content-Type": "application/json",
                                            "X-Workspace-Id": `${currentWorkspaceId}`
                                        },
                                        body: JSON.stringify({
                                            originalFileName: f.name
                                        })
                                    }
                                );

                                const { uploadSessionId, uploadURL } = await initRes.json();
                                setUploadSessionId(uploadSessionId);

                                setStep("scanning");

                                // 2️⃣ UPLOAD TO S3
                                const uploadRes = await fetch(uploadURL, {
                                    method: "PUT",
                                    headers: {
                                        "Content-Type": "text/csv",
                                        "x-amz-meta-uploadsessionid": `${uploadSessionId}`
                                    },
                                    body: f
                                });

                                if (!uploadRes.ok) {
                                    throw new Error("Upload failed");
                                }

                                // 3️⃣ IMMEDIATE SCAN CALL (NO POLLING)
                                const scanRes = await fetch(
                                    "http://172.30.107.109:8080/api/tables/import/scan",
                                    {
                                        method: "POST",
                                        headers: {
                                            Authorization: `Bearer ${token}`,
                                            "Content-Type": "application/json",
                                            "X-Workspace-Id": `${currentWorkspaceId}`
                                        },
                                        body: JSON.stringify({
                                            uploadSessionId
                                        })
                                    }
                                );

                                if (!scanRes.ok) {
                                    throw new Error("Scan failed");
                                }

                                const result = await scanRes.json();
                                setScan(result);
                                setStep("confirm");

                            } catch (err) {
                                console.error(err);
                                setStep("error");
                            }
                        }}
                    />
                ) : (
                    <Grid container>
                        <Grid size={6}>
                            {step === "scanning" && (
                                <Box
                                    sx={{
                                        height: 400,
                                        display: "flex",
                                        alignItems: "center",
                                        justifyContent: "center"
                                    }}
                                >
                                    <CircularProgress />
                                </Box>
                            )}

                            {step === "confirm" && scan && (
                                <CsvScanResultEditor
                                    scanResponse={scan}
                                    handleSubmit={handleSubmitConfirmTableRequest}
                                />
                            )}

                            {step === "error" && (
                                <Box>
                                    Upload or scan failed. Please try again.
                                </Box>
                            )}
                        </Grid>

                        <Grid size={6}>
                            <CsvPreviewTable data={preview} />
                        </Grid>
                    </Grid>
                )}
            </DialogContent>
        </Dialog>
    );
}