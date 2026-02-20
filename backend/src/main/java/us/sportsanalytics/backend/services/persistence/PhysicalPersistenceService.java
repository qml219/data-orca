package us.sportsanalytics.backend.services.persistence;

import java.util.List;
import java.util.Map;

import lombok.val;
import us.sportsanalytics.backend.models.domain.ColumnDefinition;

public interface PhysicalPersistenceService {

        void createSchema(String schemaName);

        void dropSchema(String schemaName);

        void createTable(String schemeName, String tableName, List<ColumnDefinition> columns);

        void dropTable(
                        String schema,
                        String tableName);

        void insertRow(
                        String schema,
                        String tableName,
                        Map<String, Object> values);

        // UPSERT INTO schema.tableName WHERE id = :pkValue SET
        void updateRow(
                        String schema,
                        String tableName,

                        // Composite PKs
                        Map<String, Object> pkValues,

                        Map<String, Object> values);

        void deleteRow(
                        String schema,
                        String tableName,
                        Map<String, Object> pkValues);

}
