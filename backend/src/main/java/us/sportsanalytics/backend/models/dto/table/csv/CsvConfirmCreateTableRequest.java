// The request sent from the frontend, storing user's confirmation after viewing
// the proposed schema from the csv parser scan.

package us.sportsanalytics.backend.models.dto.table.csv;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class CsvConfirmCreateTableRequest {

    @NotBlank
    private String tableName;

    private String description;

    // column names that user checked as primary key
    @NotEmpty
    private List<String> primaryKeys;

    // optional: allow user to override inferred types in UI
    private List<ColumnOverride> overrides;

    public static class ColumnOverride {
        @NotBlank
        private String columnName;

        @NotBlank
        private String dataType; // e.g. "BIGINT", "TEXT", ...

        private Boolean nullable; // optional override

        private String description;

        // getters/setters
        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public Boolean getNullable() {
            return nullable;
        }

        public void setNullable(Boolean nullable) {
            this.nullable = nullable;
        }

        public String getDescription() {
            return this.description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    // getters/setters
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(List<String> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    public List<ColumnOverride> getOverrides() {
        return overrides;
    }

    public void setOverrides(List<ColumnOverride> overrides) {
        this.overrides = overrides;
    }
}
