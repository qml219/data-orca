package us.sportsanalytics.backend.repositories.workspace;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import us.sportsanalytics.backend.models.domain.Workspace;

@Repository
public interface WorkspaceRepository extends CrudRepository<Workspace, UUID> {
    Optional<Workspace> findByName(String name);

    Optional<Workspace> findBySchemaName(String schemaName);

    boolean existsBySchemaName(String schemaName);
}
