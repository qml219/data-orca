import { useWorkspaceContext } from "@/components/contexts/workspace/workspace.context";
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select, { type SelectChangeEvent } from '@mui/material/Select';

export default function WorkspaceSwitcher() {
    const { currentWorkspaceId , allWorkspaces, switchWorkspace } = useWorkspaceContext();

    const handleWorkspaceChange = (e: SelectChangeEvent<string | null>) => {
        const idValue = e?.target.value;
        if (idValue) switchWorkspace(idValue);
    }

    return (
        // <label style={{ marginRight: '1rem'}}>
        //     Pick a workspace:
        //     <select name="selectWorkspaces" onChange={handleWorkspaceChange}>
        //         {allWorkspaces.allIds.map(id => {
        //             const ws = allWorkspaces.byId[id];
        //             return <option value={id} key={id}>{ws.name}</option>
        //         })}
        //     </select>
        // </label>

        <FormControl variant="outlined"  sx={{ m: 1, minWidth: 120
        }} size="small"> 
        <InputLabel id="select-workspace-label">Workspace</InputLabel>
        <Select
            labelId="select-workspace-label"
            id="select-workspace"
            value={currentWorkspaceId}
            onChange={handleWorkspaceChange}
            label="Workspace"
        >
            {
                allWorkspaces.allIds.map(id => {
                    const ws = allWorkspaces.byId[id];
                    return (<MenuItem value={id}>{ws.name}</MenuItem>)
                })
            }
        </Select>

        </FormControl>
    )

}