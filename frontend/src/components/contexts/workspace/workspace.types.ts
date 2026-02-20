export interface Workspace {
    id: string
    name: string
    description: string
    role: WorkspaceRole
};

export type WorkspaceRole = "OWNER" | "ADMIN" | "USER";

export type AllWorkspacesState = {
    byId: Record<string, Workspace>,
    allIds: string[]
}

export interface WorkspaceContextValue {
    allWorkspaces: AllWorkspacesState
    currentWorkspaceId: string | null,
    currentWorkspace: Workspace | null,
    role: WorkspaceRole | null,
    addWorkspace: (ws: Workspace) => void,
    refreshWorkspaces: () => Promise<void>,
    switchWorkspace: (id: string) => void
}
