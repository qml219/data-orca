package us.sportsanalytics.backend.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import us.sportsanalytics.backend.models.domain.Workspace;
import us.sportsanalytics.backend.models.dto.workspace.UserWorkspaceDto;
import us.sportsanalytics.backend.models.dto.workspace.WorkspaceRequest;
import us.sportsanalytics.backend.models.dto.workspace.WorkspaceResponse;
import us.sportsanalytics.backend.security.CustomUserDetails;
import us.sportsanalytics.backend.services.WorkspaceService;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {
    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @PostMapping("/create")
    // public WorkspaceResponse createWorkspace(@RequestBody WorkspaceRequest
    // request,
    public WorkspaceResponse createWorkspace(@Valid @ModelAttribute WorkspaceRequest request,
            @AuthenticationPrincipal CustomUserDetails user) {
        Workspace workspace = workspaceService.createWorkspace(request.getName(), request.getDescription(),
                user.getId());
        return new WorkspaceResponse(workspace.getId(), workspace.getName(), workspace.getDescription());
    }

    @GetMapping
    public List<UserWorkspaceDto> getUserWorkspaces(@AuthenticationPrincipal CustomUserDetails user) {
        UUID userId = user.getId();
        return workspaceService.getUserWorkspaces(userId);
    }
}
