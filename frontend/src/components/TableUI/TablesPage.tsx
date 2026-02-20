import { Box, Stack } from '@mui/material'
import React, { useEffect, useState } from 'react'
import type { TableMetadata } from './TableCardsGrid/TableCard/TableCard'
import { useAuth } from '../auth/auth.context';
import { useWorkspaceContext } from '../contexts/workspace/workspace.context';
import TableCardsGrid from './TableCardsGrid/TableCardsGrid';
import ImportTableButton from '../ImportTableButton/ImportTableButton';

export type AllTablesState = {
    byId: Record<string, TableMetadata>
    allIds: string[]
}

export function normalizeTableState(data: any[]): AllTablesState {

    const byId: Record<string, TableMetadata> = {};
    const allIds: string[] = [];

    data.forEach((d) => {
            byId[d.id] = {
                tableName: d.tableName,
                tableDescription: d.description,
                tableCreatedAt: d.createdAt
            }
            allIds.push(d.id);
        }
    )

    return { byId, allIds }
}


export default function TablesPage({
    headers, 
    controls,
    // children
}: {
    headers: React.ReactNode,
    controls: React.ReactNode,
    // children: React.ReactNode
}){
    const { token } = useAuth();
    const { currentWorkspaceId } = useWorkspaceContext();
    const [tables, setTables] = useState<AllTablesState>({
        byId: {},
        allIds: []
    })
    
    const fetchAllTables = async () => {
        const response = await fetch("http://172.30.107.109:8080/api/tables", 
            {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`,
                    "X-Workspace-Id": `${currentWorkspaceId}`
                }
            }
        )

        if (!response.ok) {
            console.error("Failed to fetch tables")
            return;
        }

        const responseData = await response.json()
        console.log(responseData);

        return responseData
    }

    useEffect(() => {
    const fetchAllTablesAsync = async () => {
        const data = await fetchAllTables();
        const normalizedTableState = normalizeTableState(data);
        setTables(normalizedTableState);
        console.log(tables)
    };
    fetchAllTablesAsync();
}, [currentWorkspaceId]);

    return (<>
        <Box sx={{ p: 3}}>
            <Stack spacing={2}>
                {headers}
                {controls}
                {/* {children} */}
                <TableCardsGrid
                    tableMetadatas={Array.from(Object.values(tables.byId))} onTableClick={() => { console.log("You clicked on Table ")}}
                >

                </TableCardsGrid>
            </Stack>
        </Box>
    </>)
}