import type { Workspace } from "./workspace.types";

export function normalizeWorkspaces(workspaces: Workspace[]) {
    const byId: Record<string, Workspace> = {};
    const allIds: string[] = []

    for (const ws of workspaces) {
        byId[ws.id] = ws;
        allIds.push(ws.id);
    }

    return { byId, allIds }
}