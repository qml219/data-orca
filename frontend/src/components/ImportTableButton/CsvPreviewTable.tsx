import React, { useMemo } from "react";
import Box from "@mui/material/Box";
import { DataGrid, type GridColDef } from "@mui/x-data-grid";

interface Props {
  data: string[][];
}

function CsvPreviewTable({ data }: Props) {
  const header = useMemo(() => data[0] ?? [], [data]);

  const rows = useMemo(() => {
    const rowsRaw = data.slice(1);
    return rowsRaw.map((row, i) => {
      const obj: any = { id: i };
      header.forEach((colName, idx) => {
        obj[colName] = row[idx];
      });
      return obj;
    });
  }, [data, header]);

  const columns = useMemo<GridColDef[]>(() => {
    return header.map((colName) => ({
      field: colName,
      headerName: colName,
      width: Math.max(120, 15 * colName.length),
      editable: false,
    }));
  }, [header]);

  return (
    <Box sx={{ height: "auto", width: "100%" }}>
      <DataGrid
        rows={rows}
        columns={columns}
        initialState={{
          pagination: {
            paginationModel: { pageSize: 10 },
          },
        }}
        pageSizeOptions={[10]}
      />
    </Box>
  );
}

export default React.memo(CsvPreviewTable);