import { Grid } from "@mui/material";
import type { TableMetadata } from "./TableCard/TableCard";
import TableCard from "./TableCard/TableCard";

export default function TableCardsGrid({ tableMetadatas, onTableClick }: {
    tableMetadatas: TableMetadata[]
    onTableClick: React.MouseEventHandler<HTMLButtonElement>
}) {
    return (
        <Grid container spacing={2}>
            {tableMetadatas.map((t) => {
                    return (<Grid size={2}>
                        <TableCard tableCardProps={{...t, onClick: onTableClick}}></TableCard>
                    </Grid>);
                }
            )
            }
        </Grid>
    )
}