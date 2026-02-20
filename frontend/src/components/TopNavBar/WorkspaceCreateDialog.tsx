import Button from "@mui/material/Button";
import Dialog from "@mui/material/Dialog";
import TextField from "@mui/material/TextField";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogContentText from "@mui/material/DialogContentText";
import DialogTitle from "@mui/material/DialogTitle";
import React, { useState } from "react";
import { useAuth } from "../auth/auth.context";
import { Snackbar } from "@mui/material";
import Alert from "@mui/material/Alert";
import { useWorkspaceContext } from "../contexts/workspace/workspace.context";
import type { Workspace } from "../contexts/workspace/workspace.types";

interface WorkspaceCreateDialogProps {
    onSuccess: () => void
    onError: () => void
}

export default function WorkspaceCreateDialog({
    onSuccess,
    onError
}: WorkspaceCreateDialogProps) {

    const { token } = useAuth();
    const { addWorkspace } = useWorkspaceContext();

    const [open, setOpen] = useState<boolean>(false);

    const handleClickOpen = () => {
        setOpen(true);
    }

    const handleClickClose = () => {
        setOpen(false);
    }

        const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
            e.preventDefault();
            const formData: FormData = new FormData(e.currentTarget);

            // console.log(formData) - FormData is not enumerable. FormData.entries() returns an iterator that can yield [k, v]. So, 
            // for (const [k, v] of fd.entries()) console.log(k, v);

            console.log(Object.fromEntries(formData));

            const request = await fetch("http://172.30.107.109:8080/api/workspaces/create", 
                {
                    method: "POST", 
                    headers: {
                        Authorization: `Bearer ${token}`
                    },
                    body: formData
                }
            )

        if (!request.ok) {
            onError();
            return;
        }
        
        const responseData = await request.json();
        const workspace = responseData as Workspace;
        addWorkspace(workspace);
        onSuccess();
        setOpen(false);
    }
    
    return (
        // <> </> : react fragments
        <React.Fragment> 
            <Button variant="contained" onClick={handleClickOpen} size="medium" 
            sx={{ height: 40}}
            >
                Create a new workspace 
            </Button>
            <Dialog
                open={open} onClose={handleClickClose}
            >
                <DialogTitle>New Workspace</DialogTitle>
                <DialogContent>
                    <DialogContentText>Create a new workspace that encapsulates your data domain</DialogContentText>
                    <form onSubmit={handleSubmit} id="workspace-create-form">
                        <TextField
                            required
                            margin="dense"
                            id="name"
                            name="name" // included in the actual HTTP request body as key
                            label="Workspace Name"
                            type="text"
                            fullWidth
                            variant="standard"
                        />
                        <TextField
                            margin="dense"
                            id="description"
                            name="description"
                            label="Description"
                            type="text"
                            fullWidth
                            variant="standard"
                        />
                    </form>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClickClose}>Cancel</Button>
                    <Button type="submit" form="workspace-create-form">
                        Create
                    </Button>
                </DialogActions>
            </Dialog>
        </React.Fragment>
    )
}