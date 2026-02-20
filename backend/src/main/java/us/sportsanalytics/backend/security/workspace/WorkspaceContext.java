package us.sportsanalytics.backend.security.workspace;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import us.sportsanalytics.backend.models.domain.roles.WorkspaceRole;

@Component
// Inject a proxy into longer-scoped Service Beans knows how to resolve
// shorter-scoped, per-request beans
// Equivalent to @Scope(value=WebApplicationContext.SCOPE_REQUEST, proxyMode =
// ScopedProxyMode.TARGET_CLASS)
@RequestScope
@Getter
@RequiredArgsConstructor
public class WorkspaceContext {
    private UUID workspaceId;
    private String schemaName;
    private WorkspaceRole role;

    void setWorkspaceId(UUID workspaceId) {
        this.workspaceId = workspaceId;
    }

    void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    void setRole(WorkspaceRole role) {
        this.role = role;
    }
}
