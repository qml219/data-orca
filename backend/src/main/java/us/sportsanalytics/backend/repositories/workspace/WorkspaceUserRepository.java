package us.sportsanalytics.backend.repositories.workspace;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import us.sportsanalytics.backend.models.domain.roles.WorkspaceRole;
import us.sportsanalytics.backend.models.dto.workspace.UserWorkspaceDto;

public interface WorkspaceUserRepository {
        void addUserToWorkspace(
                        UUID userId,
                        UUID workspaceId,
                        WorkspaceRole role);

        boolean isMember(
                        UUID userId,
                        UUID workspaceId);

        Optional<WorkspaceRole> findUserRole(
                        UUID userId,
                        UUID workspaceId);

        List<UserWorkspaceDto> findUserWorkspaces(UUID userId);
}
