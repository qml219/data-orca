package us.sportsanalytics.backend.repositories.table;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.repository.CrudRepository;

import us.sportsanalytics.backend.models.domain.TableDefinition;

public interface TableDefinitionRepository extends JpaRepository<TableDefinition, UUID> {

    Optional<TableDefinition> findBySchemaName(String schemaName);

    Optional<TableDefinition> findByTableName(String tableName);

    List<TableDefinition> findAllBySchemaName(String schemaName);

}
