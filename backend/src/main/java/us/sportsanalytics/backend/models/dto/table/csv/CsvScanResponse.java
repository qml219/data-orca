package us.sportsanalytics.backend.models.dto.table.csv;

import java.util.List;

public record CsvScanResponse(
                String suggestedTableName,
                List<CsvColumnProposal> columns) {
        public record CsvColumnProposal(
                        String columnName,
                        String suggestedDataType, // Postgres type string like "BIGINT", "DOUBLE PRECISION", "BOOLEAN",
                                                  // "TIMESTAMP", "TEXT"
                        boolean nullable) {
        }
}
