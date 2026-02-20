import { useState } from "react";
import { validDataTypes, type TableColumn } from "./CreateTableDialog";
import { Box, Button, Checkbox, FormControl, FormControlLabel, InputLabel, MenuItem, Select, TextField } from "@mui/material";

export default function ColumnDefRow ({
  col,
  onChange,
  onRemove,
}: {
  col: TableColumn;
  onChange: <K extends keyof TableColumn>(
    id: string,
    field: K,
    value: TableColumn[K]
  ) => void;
  onRemove: (id: string) => void;
}) {

  const [touched, setTouched] = useState(false);

  return (
    <Box
        sx={{
            alignItems: "flex-start",
            display: "flex",
            flexDirection: "row",
            gap: 1,
            marginTop: 2
        }}
    >

    <TextField
        required
        margin="dense"
        size="medium"
        variant="outlined"
        id={`column-name-tf-${col.id}`}
        label="Column Name"
        value={col.columnName}
        error={touched 
            && col.columnName.trim() === ""
        }
        helperText={
        touched 
        && col.columnName.trim() === "" 
            ? "Required" : ""
        }
        onBlur={() => setTouched(true)}
        onChange={(e) =>
        onChange(col.id, "columnName", e.target.value)
        }
        slotProps={{ 
            inputLabel: { shrink: true },
        }}
        sx={{ flex: "2.5 1 0%"}}
    />
    <FormControl variant="outlined" size="medium" margin="dense" 
        sx={{ flex: "2 1 0%"}}
    >
        <InputLabel shrink>Data Type</InputLabel>
        <Select
            id={`data-type-sl-${col.id}`}
            value={col.dataType}
            label="Data Type"
            onChange={(e) =>
                onChange(col.id, "dataType", e.target.value)
            }
            displayEmpty // looks at value="" => Find MenuItem with "" value and render
            >
                {/* // disabled so can't be reselected */}
                <MenuItem value="" disabled>
                    Select a type
                </MenuItem>
            {validDataTypes.map((type) => (
                <MenuItem key={type} value={type}>
                {type}
                </MenuItem>
        ))}
        </Select>
    </FormControl>
    <FormControlLabel id={`primary-key-cb-${col.id}`}
        control={
            <Checkbox 
                checked={col.isPrimary}
                onChange={(e) =>
                    onChange(col.id, "isPrimary", e.target.checked)
                }
            />
        }
        label="Primary Key"
        labelPlacement="top"
        slotProps={{ typography: { fontSize: "0.75rem" } }}
        sx={{ alignSelf: "center", flex: "0 0 auto"}} // ignore the flex parent rule
    />
    <FormControlLabel id={`nullable-cb-${col.id}`} 
        control={
            <Checkbox 
                checked={col.isNullable}
                onChange={(e) =>
                    onChange(col.id, "isNullable", e.target.checked)
                }
            />
        }
        label="Is Nullable"
        labelPlacement="top"
        slotProps={{ typography: { fontSize: "0.75rem" } }}
        sx={{ alignSelf: "center", flex: "0 0 auto"}} // ignore the flex parent rule
    />
    <TextField
        multiline
        margin="dense"
        id={`description-tf-${col.id}`}
        label="Column Description"
        value={col.description}
        onChange={(e) => onChange(col.id, "description", e.target.value)}
        sx={{ flex: "3 1 0%"}}
    ></TextField>


    <Button 
        variant="contained"
        color="info"
        onClick={() => onRemove(col.id)}
        sx={{ alignSelf: "center", flex: "0 0 auto"}}
    >❌</Button>
    
    </Box>
  );
}