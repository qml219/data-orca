export async function fetchWorkspaces(token: string) {

    // fetch workspaces accessible by the user
    const workspacesResponse = await fetch("http://172.30.107.109:8080/api/workspaces", {
        method: 'GET',
        headers: {
            "Authorization": `Bearer ${token}`
        },
    });

    if (!workspacesResponse.ok) {
        const errorText = await workspacesResponse.text();
        throw new Error(errorText || `Failed to fetch workspaces with status: ${workspacesResponse.status}`)
    }

    return workspacesResponse.json();
}