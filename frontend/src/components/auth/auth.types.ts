import type { Workspace } from "../contexts/workspace/workspace.types";
import type { UserDTO } from "./user/user.dto";

export interface AuthContextValue {
    user: UserDTO | null // when user hasn't logged in both token & user can be nullable  
    token: string | null 

    // workspaces: Record<string, Workspace>
    // workspaceIds: string[]

    // addWorkspace: (ws: Workspace) => void
    // refreshWorkspaces: () => Promise<void>

    isAuthenticated: boolean
    loading: boolean

    login: (identifier: string, password: string) => Promise<void>
    logout: () => void,
}