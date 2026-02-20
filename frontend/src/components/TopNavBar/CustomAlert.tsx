import React from 'react';
import Button from '@mui/material/Button';
import Snackbar, { type SnackbarCloseReason } from '@mui/material/Snackbar';
import Alert from '@mui/material/Alert';

export type AlertSeverity = "success" | "error" | "warning" | "info"

export default function CustomAlert({
    open,
    severity,
    message,
    handleClose 
}: { open: boolean, message: string, severity: AlertSeverity
     handleClose: () => void 
}) {
    
    return (
        <Snackbar open={open} autoHideDuration={3000} 
        onClose={handleClose}
        >
            <Alert 
                onClose={handleClose}
                severity={severity}
                variant="filled"
                sx={{ width: '100%'}}
            >
                {message}
            </Alert>
        </Snackbar> 
    )

}