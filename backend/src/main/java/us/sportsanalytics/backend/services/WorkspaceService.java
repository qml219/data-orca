package us.sportsanalytics.backend.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import us.sportsanalytics.backend.models.domain.Workspace;
import us.sportsanalytics.backend.models.domain.roles.WorkspaceRole;
import us.sportsanalytics.backend.models.dto.workspace.UserWorkspaceDto;
import us.sportsanalytics.backend.repositories.workspace.WorkspaceRepository;
import us.sportsanalytics.backend.repositories.workspace.jdbc.JdbcWorkspaceUserRepository;
import us.sportsanalytics.backend.services.persistence.JdbcPhysicalPersistenceServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final JdbcWorkspaceUserRepository workspaceUserRepository;
    private final JdbcPhysicalPersistenceServiceImpl persistenceService;

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    public Workspace createWorkspace(String name, String description, UUID userId) {
        String schemaName = generateWorkspaceSchemaName();

        Workspace workspace = workspaceRepository.save(new Workspace(null, name, schemaName, description));

        workspaceUserRepository.addUserToWorkspace(userId, workspace.getId(), WorkspaceRole.OWNER);

        persistenceService.createSchema(schemaName);

        return workspace;
    }

    public List<UserWorkspaceDto> getUserWorkspaces(UUID userId) {

        // List<UserWorkspaceDto> workspaces =
        // workspaceUserRepository.findWorkspacesForUser(userId).stream()
        // .map(id -> {
        // String name = workspaceRepository.findById(id).get().getName();
        // WorkspaceRole role = workspaceUserRepository.findUserRole(userId, id).get();
        // return new UserWorkspaceDto(id, name, role);
        // })
        // .collect(Collectors.toList());

        // Above code makes the mistake of N+1 queries, avoid by writing direct method
        // from WorspaceUserRepository - jdbc sql

        return workspaceUserRepository.findUserWorkspaces(userId);

    }

    public String generateWorkspaceSchemaName() {
        return "ws_" + UUID.randomUUID().toString().replace("-", "");
    }
}
