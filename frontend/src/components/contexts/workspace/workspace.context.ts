import { createContext, useContext } from "react";
import type { WorkspaceContextValue } from "./workspace.types";

export const WorkspaceContext = createContext<WorkspaceContextValue | null>(null);

export function useWorkspaceContext(): WorkspaceContextValue {
    
    const cts = useContext(WorkspaceContext);

    if (!cts) {
        throw new Error('useWorkspaceContext has to be within a Provider')
    }

    return cts

}