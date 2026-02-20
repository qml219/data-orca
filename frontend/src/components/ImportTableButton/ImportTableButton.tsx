import Button from "@mui/material/Button";
import { useState } from "react";
import { ImportCsvModal } from "./ImportCsvModal";

export default function ImportTableButton() {
    const [modalOpen, setModalOpen] = useState<boolean>(false);

    return (
        <>
            <Button variant="outlined" onClick={() => setModalOpen(true)} sx={{ maxWidth: '10%' }}>
                Import Table
            </Button>
            <ImportCsvModal open={modalOpen} onClose={() => setModalOpen(false)}></ImportCsvModal>
        </>
    )
}