package us.sportsanalytics.backend.models.dto.workspace;

import java.util.UUID;

import us.sportsanalytics.backend.models.domain.roles.WorkspaceRole;

// This is the API contract between FE and BE about the list of workspaces accessible by the user, populated post-login
public record UserWorkspaceDto(
        UUID id,
        String name,
        String description,
        WorkspaceRole role) {
}