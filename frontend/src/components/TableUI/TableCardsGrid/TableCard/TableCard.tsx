import { Card, CardActionArea, CardContent, CardHeader, Typography } from "@mui/material"
import TableRowsIcon from '@mui/icons-material/TableRows';

export type TableMetadata = {
    tableName: string,
    tableDescription: string, 
    // tableColumnCount: number,
    // lastModified: Date,
    // tableCount: number,
    tableCreatedAt: string
}

export interface TableCardProps extends TableMetadata {
    onClick: React.MouseEventHandler<HTMLButtonElement>
}

export default function TableCard({ tableCardProps }: {tableCardProps: TableCardProps}) {
    return (
    <Card>
        <CardActionArea>
            <CardHeader title={"Table"} avatar={     <TableRowsIcon/>}>
            </CardHeader>
            <CardContent>
                <Typography variant="h6" sx={{ my: "4px"}}>{tableCardProps.tableName}</Typography>
                {/* <Typography variant="body2" sx={{ my: "1em"}}>Column Count:{tableCardProps.tableColumnCount}</Typography>
                 <Typography variant="body2" sx={{ my: "4px"}}>Count:{tableCardProps.tableCount}</Typography> */}
                <Typography variant="body2" sx={{ my: "1em"}}>Description: {tableCardProps.tableDescription}</Typography>
                <Typography variant="caption" sx={{ my: "1em"}}>Created At:{new Date(tableCardProps.tableCreatedAt).toDateString()}</Typography>
            </CardContent>
        </CardActionArea>
    </Card>
    )
}