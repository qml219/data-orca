import { useState } from "react";
import type { TableColumn } from "../Tables/CreateTableDialog";
import type { ColumnOverride, CsvScanResponse } from "./ImportCsvModal";
import { nanoid } from "nanoid";
import { Box, Button, TextField } from "@mui/material";
import ColumnDefRow from "../Tables/ColumnDefRow";

export interface CsvScanResultEditorProps {
    scanResponse: CsvScanResponse
    handleSubmit: (payload: {
        tableName: string,
        description: string,
        primaryKeys: string[],
        columns: ColumnOverride[];
    }) => Promise<void> | void;
}

export default function CsvScanResultEditor({ 
    scanResponse, handleSubmit
}: CsvScanResultEditorProps) {
    const [tableName, setTableName] = useState<string>(
        scanResponse ? scanResponse.suggestedTableName : ""
    );
    const [description, setDescription] = useState<string>("");
    const [tableColumns, setTableColumns] = useState<TableColumn[]>(
        scanResponse ? scanResponse.columns.map((col) => {
            return {
                id: nanoid(),
                columnName: col.columnName,
                dataType: col.suggestedDataType,
                isPrimary: false,
                isNullable: col.nullable,
                description: ""
            }
        }) : []
    );

    const handleClickAddColumn = () => {
        setTableColumns([...tableColumns, { id: nanoid(), columnName: "", dataType: "", isPrimary: false, isNullable: false, description: "" }]);
    }

    const handleRemoveColumn = (id: string) => {
        setTableColumns(tableColumns.filter((col) => col.id != id))
    }

    const handleColumnChange = <K extends keyof TableColumn>(id: string, field: K, value: TableColumn[K]) => {
        setTableColumns(tableColumns.map((col) => col.id === id ? {...col, [field]: value} : col))
    }

    const mapTableColumnsToOverrides = (tableColumns: TableColumn[]) => {
        return tableColumns.map((col) => {
            return {
                columnName: col.columnName,
                dataType: col.dataType,
                nullable: col.isNullable,
                description: col.description
            }
        })
    }


    return (
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
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                type="text"
                fullWidth
                variant="standard"
            />

            {
                tableColumns.map((col) => (
                    <ColumnDefRow
                        key={col.id}
                        col={col}
                        onChange={handleColumnChange}
                        onRemove={handleRemoveColumn}
                    />
                ))
                // <div>Column count: {tableColumns.length}</div>
            }

            <Button variant="outlined" onClick={handleClickAddColumn} size="small">Add Column</Button>
            <Button variant="contained" color="success" onClick={() => handleSubmit({
                tableName: tableName,
                description: description,
                primaryKeys: tableColumns.filter(col => col.isPrimary).map(col => col.columnName),
                columns: mapTableColumnsToOverrides(tableColumns)
                }
            )}
            >Submit</Button>
        </Box>
    )
}