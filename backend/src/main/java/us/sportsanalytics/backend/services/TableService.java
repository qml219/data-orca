package us.sportsanalytics.backend.services;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import us.sportsanalytics.backend.models.domain.ColumnDefinition;
import us.sportsanalytics.backend.models.domain.TableDefinition;
import us.sportsanalytics.backend.models.dto.table.CreateColumnRequest;
import us.sportsanalytics.backend.models.dto.table.CreateTableRequest;
import us.sportsanalytics.backend.repositories.table.ColumnDefinitionRepository;
import us.sportsanalytics.backend.repositories.table.TableDefinitionRepository;
import us.sportsanalytics.backend.repositories.workspace.WorkspaceRepository;
import us.sportsanalytics.backend.security.workspace.WorkspaceContext;
import us.sportsanalytics.backend.services.persistence.JdbcPhysicalPersistenceServiceImpl;

@Service
@RequiredArgsConstructor
public class TableService {
    private final PostgresTypeRegistry postgresTypeRegistry;

    private final JdbcPhysicalPersistenceServiceImpl persistenceService;
    private final TableDefinitionRepository tableRepository;
    private final ColumnDefinitionRepository columnRepository;
    private final WorkspaceContext workspaceContext;

    public List<TableDefinition> getAllTablesMetadatas() {
        return tableRepository.findAllBySchemaName(workspaceContext.getSchemaName());
    }

    // If a column failed to safe, the whole operation is roll backed.
    @Transactional
    public TableDefinition saveTable(CreateTableRequest request) {

        // if (!workspaceRepository.existsBySchemaName(userWorkspaceSchemaName)) {
        // throw new IllegalArgumentException("Invalid schema name");
        // }

        TableDefinition tableDef = new TableDefinition();
        tableDef.setTableName(request.getName());
        tableDef.setSchemaName(workspaceContext.getSchemaName());
        tableDef.setCreatedAt(Instant.now());
        tableDef.setDescription(request.getDescription());

        TableDefinition newTable = tableRepository.save(tableDef);

        List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();

        for (CreateColumnRequest newColRequest : request.getColumns()) {
            columns.add(saveColumn(newColRequest, newTable));
        }

        persistenceService.createTable(newTable.getSchemaName(), newTable.getTableName(), columns);

        return newTable;

    }

    public ColumnDefinition saveColumn(CreateColumnRequest request, TableDefinition table) {

        ColumnDefinition newColumn = new ColumnDefinition();
        newColumn.setColumnName(request.getColumnName());

        // if (!postgresTypeRegistry.validateDataType(request.getDataType())) {
        // throw new IllegalArgumentException("Invalid data type: " +
        // request.getDataType());
        // }

        newColumn.setDataType(request.getDataType());
        newColumn.setAllowNull(request.getIsNullable());
        newColumn.setPrimaryKey(request.getIsPrimary());
        newColumn.setDescription(request.getDescription());
        newColumn.setTable(table);
        newColumn.setCreatedAt(Instant.now());
        newColumn.validate();

        return columnRepository.save(newColumn);
    }

}
