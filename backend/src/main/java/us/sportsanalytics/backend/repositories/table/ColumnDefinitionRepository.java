package us.sportsanalytics.backend.repositories.table;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.repository.CrudRepository;

import us.sportsanalytics.backend.models.domain.ColumnDefinition;

public interface ColumnDefinitionRepository
        extends JpaRepository<ColumnDefinition, UUID> {

    // This works because JPA parses on Java object property names
    // So findBy [table].[id] -> [table] is TableDefinition.id
    // SELECT c from ColumnDefinition c where c.table.id = :tableId - JPQL
    Optional<ColumnDefinition> findByTableId(UUID tableId);

    // SELECT c FROM ColumnDefinition c WHERE c.table.id = :tableId and c.columnName
    // = :columnName
    Optional<ColumnDefinition> findByTableIdAndColumnName(UUID tableId, String columnName);

    // SELECT EXISTS (SELEC CASE WHEN COUNT(c) > 0 then true ELSE false END FROM
    // ColumnDefinition c WHERE c.table.id = :tableId AND c.columnName =
    // :columnName)
    boolean existsByTableIdAndColumnName(UUID tableId, String columnName);
}
