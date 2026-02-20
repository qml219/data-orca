import Box from '@mui/material/Box';
import { DataGrid, type GridColDef } from '@mui/x-data-grid';

function convertToRows(header: string[], rows: string[][]) {
    return rows.map((row, i) => {
        const obj: any = { id: i };
        header.forEach((colName, i) => {
            obj[colName] = row[i];
        })
        return obj;
    });
}

export default function CsvPreviewTable({ data }: { data: string[][] }) {

    // let header = data.shift() ?? [];

    let header = data[0] ?? [];
    
    // const rows = data;
    const rowsRaw = data.slice(1);

    const rows = convertToRows(header, rowsRaw);

    console.log(rows);

    let columns: GridColDef<(typeof rows)[number]>[] = [];
    
    for (const colName of header) {
        columns.push({
            field: colName,
            headerName: colName,
            width: Math.max(120 , 15 * colName.length),
            editable: false
        })
    }

    return (
        <Box sx={{ height: 'auto', width: '100%' }}>
            <DataGrid
                rows={rows}
                columns={columns}
                initialState={{
                pagination: {
                    paginationModel: {
                        pageSize: 10,
                    },
                },
                }}
                pageSizeOptions={[10]}
                // checkboxSelection
                // disableRowSelectionOnClick
            />
        </Box>
    );
}