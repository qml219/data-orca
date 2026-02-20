import { Box, Checkbox, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, FormControl, Input, InputLabel, MenuItem, Select, TextField } from "@mui/material";
import Button from "@mui/material/Button";
import { useEffect, useState } from "react";
import { nanoid } from "nanoid"
import ColumnDefRow from "./ColumnDefRow";

export interface TableColumn {
    id: string
    columnName: string
    dataType: string
    isPrimary: boolean
    isNullable: boolean
    description: string
}

export const validDataTypes = [
    "DOUBLE PRECISION",
  "SERIAL",
  "INTEGER",
  "VARCHAR(255)",
  "TEXT",
  "BOOLEAN",
  "DATE",
  "FLOAT"
]

export interface CreateTableDialogProps {
    open: boolean,
    handleClose: () => void,
    handleSubmit: (payload: { 
        name: string,
        description: string, 
        columns: TableColumn[];
    }) => Promise<void> | void;
}

export default function CreateTableDialog({
    open,
    handleClose,
    handleSubmit
}: CreateTableDialogProps) {

    const [tableName, setTableName] = useState<string>("");
    const [tableDescription, setTableDescription] = useState<string>("");
    const [tableColumns, setTableColumns] = useState<TableColumn[]>([]);

    const handleClickAddColumn = () => {
        setTableColumns([...tableColumns, { id: nanoid(), columnName: "", dataType: "", isPrimary: false, isNullable: false, description: "" }]);
    }

    const handleRemoveColumn = (id: string) => {
        setTableColumns(tableColumns.filter((col) => col.id != id))
    }

    const resetTables = () => {
        setTableColumns([
            {
            id: nanoid(),
            columnName: "",
            dataType: "",
            isPrimary: false,
            isNullable: false,
            description: ""
            }
        ]);
    }



    const handleColumnChange = <K extends keyof TableColumn>(id: string, field: K, value: TableColumn[K]) => {
        // const next = [...tableColumns];
        // // Structural Sharing - create a new object reference for next[idx] with assignment and object spread, instead of
        // // mutate in-place: next[idx][field] = value;
        // next[string] = {...next[idx], [field]: value}
        setTableColumns(tableColumns.map((col) => col.id === id ? {...col, [field]: value} : col));
    }

    useEffect(() => {
        if (open) resetTables()
    }, [open]);


    return (
        <Dialog open={open} onClose={handleClose} fullWidth maxWidth="xl">
            <DialogTitle>
                New Table
            </DialogTitle>
            <DialogContent>
                <DialogContentText>
                    Create table to persist your data
                </DialogContentText>
                <Box 
                    component="form"
                    noValidate
                    autoComplete="off"
                > 
                <TextField
                    required
                    margin="dense"
                    id="tableName"
                    label="Table Name"
                    value={tableName}
                    onChange={(e) => setTableName(e.target.value)}
                    type="text"
                    fullWidth
                    variant="standard"
                    slotProps={{ inputLabel: { shrink: true }}}
                />

                <TextField
                    margin="dense"
                    id="tableDescription"
                    label="Table Description"
                    value={tableDescription}
                    onChange={(e) => setTableDescription(e.target.value)}
                    type="text"
                    fullWidth
                    variant="standard"
                />

                {/* A column header to replace individual labels - MAYBE*/}
                {/* <Box
                    sx={{
                        display: "flex",
                        gap: 1,
                        px: 1,
                        mt: 2,
                        fontSize: "0.75rem",
                        color: "text.secondary",
                        fontWeight: 500,
                    }}
                >
                    <Box sx={{ flex: "2 1 0%" }}>Column Name</Box>
                    <Box sx={{ flex: "2 1 0%" }}>Data Type</Box>
                    <Box sx={{ width: 90, textAlign: "center" }}>Primary</Box>
                    <Box sx={{ width: 90, textAlign: "center" }}>Nullable</Box>
                    <Box sx={{ flex: "3 1 0%" }}>Description</Box>
                    <Box sx={{ width: 48 }} />
                </Box> */}


                {tableColumns.map((col) => (
                    <ColumnDefRow
                        key={col.id}
                        col={col}
                        onChange={handleColumnChange}
                        onRemove={handleRemoveColumn}
                    />
                ))}

                </Box>

                <Button variant="outlined" onClick={handleClickAddColumn} size="small">Add Column</Button>
            </DialogContent>
            <DialogActions>
                <Button type="button" onClick={handleClose} variant="contained" color="error">Cancel</Button>
                <Button type="button" onClick={async () => { await handleSubmit({
                    name: tableName, description: tableDescription, columns: tableColumns
                })}} variant="contained" color="success">Submit</Button>
            </DialogActions>
        </Dialog>
    )
}