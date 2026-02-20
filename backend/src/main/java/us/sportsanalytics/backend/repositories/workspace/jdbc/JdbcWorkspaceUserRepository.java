package us.sportsanalytics.backend.repositories.workspace.jdbc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
// import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import us.sportsanalytics.backend.models.domain.roles.WorkspaceRole;
import us.sportsanalytics.backend.models.dto.workspace.UserWorkspaceDto;
import us.sportsanalytics.backend.repositories.workspace.WorkspaceUserRepository;

@Repository
@RequiredArgsConstructor
public class JdbcWorkspaceUserRepository implements WorkspaceUserRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addUserToWorkspace(UUID userId, UUID workspaceId, WorkspaceRole role) {

        String sql = """
                    INSERT INTO workspace_user (workspace_id, user_id, role, joined_at) VALUES(?, ?, ?, now())
                """;
        jdbcTemplate.update(sql, workspaceId, userId, role.name());

    }

    @Override
    public boolean isMember(UUID userId, UUID workspaceId) {
        String sql = """
                    SELECT EXISTS (
                        SELECT 1
                        FROM workspace_user
                            where user_id = ? and workspace_id = ?
                    )
                """;
        return jdbcTemplate.queryForObject(sql, Boolean.class, userId, workspaceId);
    }

    @Override
    public Optional<WorkspaceRole> findUserRole(UUID userId, UUID workspaceId) {

        String sql = """
                    SELECT role
                    FROM workspace_user
                        where user_id = ? and workspace_id = ?
                """;

        return jdbcTemplate.query(sql,
                rs -> {
                    if (!rs.next()) {
                        return Optional.empty();
                    }
                    return Optional.of(WorkspaceRole.valueOf(rs.getString("role")));
                },
                userId,
                workspaceId);
    }

    @Override
    public List<UserWorkspaceDto> findUserWorkspaces(UUID userId) {

        String sql = """
                SELECT w.id, w.name, w.description, wu.role
                FROM workspace_user wu
                JOIN workspaces w
                    ON w.id = wu.workspace_id
                where wu.user_id = ?
                """;

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new UserWorkspaceDto(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("name"),
                        rs.getString("description"),
                        WorkspaceRole.valueOf(rs.getString("role"))),
                userId);

    }

    // The above method rewritten with JPQL - only works on JPA-managed entities
    // @Entity tag
    // @Query("""
    // SELECT new com.yourpkg.dto.UserWorkspaceDto(
    // w.id,
    // w.name,
    // wu.role
    // )
    // FROM WorkspaceUser wu
    // JOIN wu.workspace w
    // WHERE wu.user.id = :userId
    // """)
    // List<UserWorkspaceDto> findUserWorkspaces(UUID userId);

}
