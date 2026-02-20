package us.sportsanalytics.backend.models.dto.workspace;

import java.util.UUID;

// import us.sportsanalytics.backend.models.domain.roles.WorkspaceRole;

public record WorkspaceResponse(
        UUID id,
        String name,
        String description) {
}
