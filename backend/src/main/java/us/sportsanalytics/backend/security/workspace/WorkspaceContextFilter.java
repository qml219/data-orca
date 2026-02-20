package us.sportsanalytics.backend.security.workspace;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import us.sportsanalytics.backend.models.domain.Workspace;
import us.sportsanalytics.backend.models.domain.roles.WorkspaceRole;
import us.sportsanalytics.backend.repositories.workspace.WorkspaceRepository;
import us.sportsanalytics.backend.repositories.workspace.WorkspaceUserRepository;
import us.sportsanalytics.backend.security.CustomUserDetails;

@RequiredArgsConstructor
public class WorkspaceContextFilter extends OncePerRequestFilter {

    private final WorkspaceUserRepository workspaceUserRepository;

    private final WorkspaceRepository workspaceRepository;

    private final WorkspaceContext workspaceContext;

    private static final Logger log = LoggerFactory.getLogger(WorkspaceContextFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String workspaceHeader = request.getHeader("X-Workspace-Id");

        System.err.println(workspaceHeader);

        if (workspaceHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // If authentication is absent or not derived from our JWT mechanism,
        // this filter does not apply (authentication is enforced elsewhere).
        if (SecurityContextHolder.getContext().getAuthentication() == null ||
                !SecurityContextHolder.getContext().getAuthentication().isAuthenticated() || !(SecurityContextHolder
                        .getContext().getAuthentication().getPrincipal() instanceof CustomUserDetails)) {

            filterChain.doFilter(request, response);
            return;
        }

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        UUID userId = userDetails.getId();

        try {

            UUID workspaceId = UUID.fromString(workspaceHeader);

            if (!workspaceUserRepository.isMember(userId, workspaceId)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            Workspace workspace = workspaceRepository.findById(workspaceId)
                    .orElseThrow(() -> new IllegalArgumentException("Workspace Not Found"));

            String schemaName = workspace.getSchemaName();

            WorkspaceRole role = workspaceUserRepository.findUserRole(userId, workspaceId)
                    .orElseThrow(() -> new IllegalArgumentException("Workspace Role Not Found"));

            workspaceContext.setWorkspaceId(workspaceId);
            workspaceContext.setSchemaName(schemaName);
            workspaceContext.setRole(role);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.warn("Workspace access denied: userId={}, workspaceId={}, error: {}", userId, workspaceHeader,
                    e);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

    }
}
