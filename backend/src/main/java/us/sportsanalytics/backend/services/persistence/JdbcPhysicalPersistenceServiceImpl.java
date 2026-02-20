package us.sportsanalytics.backend.services.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import us.sportsanalytics.backend.models.domain.ColumnDefinition;

@Service
public class JdbcPhysicalPersistenceServiceImpl implements PhysicalPersistenceService {

    private final JdbcTemplate jdbcTemplate;

    private final static String IDENT_REGEX = "[a-zA-Z_][a-zA-Z0-9_]{0,62}";

    // Guard against sql injections through identifiers (schemaName, tableName)
    private String validateIdentifier(String ident) {
        if (!ident.matches(IDENT_REGEX)) {
            throw new IllegalArgumentException("Invalid SQL identifier: " + ident);
        }
        return ident;
    }

    private String q(String ident) {
        return "\"" + validateIdentifier(ident) + "\"";
    }

    public JdbcPhysicalPersistenceServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createSchema(String schemaName) {
        String sql = "CREATE SCHEMA IF NOT EXISTS " + q(schemaName);
        jdbcTemplate.execute(sql);
    }

    @Override
    public void dropSchema(String schemaName) {
        String sql = "DROP SCHEMA " + q(schemaName);
        jdbcTemplate.execute(sql);
    }

    @Override
    public void createTable(String schemaName, String tableName, List<ColumnDefinition> columns) {
        // String sql = String.format("CREATE TABLE IF NOT EXISTS %s.%s", schemaName,
        // tableName);
        // jdbcTemplate.execute(sql);

        String columnDefs = columns.stream().map((c) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(q(c.getColumnName()))
                    .append(" ")
                    .append(c.getDataType());

            if (!c.getAllowNull()) {
                sb.append(" NOT NULL");
            }

            return sb.toString();
        }).collect(Collectors.joining(",\n"));

        String pk_columns = columns.stream().filter(ColumnDefinition::getPrimaryKey)
                .map((c) -> q(c.getColumnName())).collect(Collectors.joining(", "));

        String sql = String.format("""
                    CREATE TABLE %s.%s (
                        %s,
                        CONSTRAINT %s PRIMARY KEY (%s)
                    )
                """, q(schemaName), q(tableName), columnDefs, q("pk_" + tableName), pk_columns);

        jdbcTemplate.execute(sql);

    }

    @Override
    public void dropTable(String schemaName, String tableName) {
        String sql = String.format("""
                    DROP TABLE IF EXISTS %s.%s
                """, schemaName, tableName);
        jdbcTemplate.execute(sql);
    }

    @Override
    public void insertRow(String schemaName, String tableName, Map<String, Object> row) {

        String columnSet = row.keySet().stream()
                .map(this::q)
                .collect(Collectors.joining(", "));

        String valuePlaceHolders = row.keySet().stream().map((k) -> "?").collect(Collectors.joining(", "));

        // String setClause = row.keySet().stream().map((key) -> key + " = ?
        // ").collect(Collectors.join(", "));

        String sql = String.format("""
                    INSERT INTO %s.%s (%s)
                    VALUES (%s)
                """, q(schemaName), q(tableName), columnSet, valuePlaceHolders);
        jdbcTemplate.update(sql, row.values().toArray());

    }

    @Override
    public void updateRow(String schemaName, String tableName, Map<String, Object> pkValues, Map<String, Object> row) {

        String setClause = row.keySet().stream()
                .map((key) -> q(key) + " = ?")
                .collect(Collectors.joining(" , "));

        String whereClause = pkValues.keySet().stream()
                .map((colName) -> q(colName) + " = ?")
                .collect(Collectors.joining(" AND "));

        String sql = String.format("""
                UPDATE %s.%s
                    SET %s
                    WHERE %s
                    """, schemaName, tableName, setClause, whereClause);

        // Flatten list of arguments - merge collections
        List<Object> args = new ArrayList<>();
        args.addAll(row.values());
        args.addAll(pkValues.values());

        jdbcTemplate.update(sql, args);

    }

    @Override
    public void deleteRow(String schemaName, String tableName, Map<String, Object> pkValues) {

        String whereClause = pkValues.keySet().stream()
                .map((colName) -> q(colName) + " = ?")
                .collect(Collectors.joining(" AND "));

        String sql = String.format("""
                DELETE FROM %s.%s
                WHERE %s
                    """, schemaName, tableName, whereClause);

        jdbcTemplate.update(sql, pkValues.values().toArray());

    }

}
