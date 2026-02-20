import { createContext, useContext, useEffect, useState } from "react";
import type { AllWorkspacesState, Workspace } from "./workspace.types";
import { useAuth } from "@/components/auth/auth.context";
import { normalizeWorkspaces } from "./workspace.utils";
import { fetchWorkspaces } from "./workspace.service";
import { WorkspaceContext } from "./workspace.context";


export function WorkspaceProvider({ children }: { children: React.ReactNode}) {
    
    const [currentWorkspaceId, setCurrentWorkspaceId] = useState<string | null>(null);
    const [allWorkspaces, setAllWorkspaces] = useState<AllWorkspacesState>({
        byId: {},
        allIds: []
    });

    const { token, isAuthenticated } = useAuth();

    const refreshWorkspaces = async (): Promise<void> => {
        if (!isAuthenticated || !token) return;
        const data = await fetchWorkspaces(token);
        setAllWorkspaces(normalizeWorkspaces(data));
    }

     // Add a new Workspace to the user's all workspaces 
    const addWorkspace = (ws: Workspace) => {
        setAllWorkspaces(prev => ({
            byId: { ...prev.byId, [ws.id]: ws},
            allIds: [...prev.allIds, ws.id]
        }))
    }

    const switchWorkspace = (workspaceId: string) => {
        if (!allWorkspaces.byId[workspaceId]) {
            console.warn("Attempt switchiting to unknown workspace:", workspaceId);
            return;
        }
        setCurrentWorkspaceId(workspaceId);
    }

    useEffect(() => {
        refreshWorkspaces();
    }, [token, isAuthenticated])


    // ?? nullish coalesce -> fallback when null or undefined 
    const currentWorkspace = currentWorkspaceId ? allWorkspaces.byId[currentWorkspaceId] ?? null : null;

    useEffect(() => {
        if (!currentWorkspace) return;
        console.log("Workspace changed:", currentWorkspace);
    }, [currentWorkspace]);
    
    // ?.role -- optional chaining, returns undefined if the accessed object is either null or undefined. Safe access
    const role = currentWorkspace?.role ?? null;

    const value = {
        allWorkspaces, 
        currentWorkspaceId,
        currentWorkspace,
        role,
        addWorkspace,
        refreshWorkspaces,
        switchWorkspace
    }

    return (
        <WorkspaceContext.Provider value={value}>
            {children}
        </WorkspaceContext.Provider>
    );

}
