package us.sportsanalytics.backend.services.persistence.csv;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import us.sportsanalytics.backend.models.dto.table.csv.CsvConfirmCreateTableRequest;
import us.sportsanalytics.backend.models.dto.table.csv.CsvScanResponse;
import us.sportsanalytics.backend.models.dto.table.CreateColumnRequest;
import us.sportsanalytics.backend.models.dto.table.CreateTableRequest;

@Service
@RequiredArgsConstructor
public class CsvIngestionService {

    private final CsvParser csvParser;

    /**
     * Save uploaded file to a temp path, infer column types, then delete.
     */
    public CsvScanResponse scanCsv(MultipartFile file, String tableNameOverride) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("CSV file is required");
        }

        // Copy file stream into temp file
        Path tmp = Files.createTempFile("upload-", ".csv");
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, tmp, StandardCopyOption.REPLACE_EXISTING);
        }

        try {
            Map<String, ColumnInference> inferred = csvParser.mapColumnTypes(tmp);

            String suggestedTableName = (tableNameOverride != null && !tableNameOverride.isBlank())
                    ? tableNameOverride
                    : stripExtension(Objects.requireNonNullElse(file.getOriginalFilename(), "uploaded_table"));

            List<CsvScanResponse.CsvColumnProposal> cols = inferred.entrySet().stream()
                    .map(e -> new CsvScanResponse.CsvColumnProposal(
                            e.getKey(),
                            toPostgresType(e.getValue().getType()),
                            e.getValue().getNullable()))
                    .toList();

            return new CsvScanResponse(suggestedTableName, cols);
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    /**
     * Build CreateTableRequest from scan result + user confirmation.
     */
    public CreateTableRequest toCreateTableRequest(CsvScanResponse scan,
            CsvConfirmCreateTableRequest confirm) {

        Map<String, CsvConfirmCreateTableRequest.ColumnOverride> overrideMap = Optional
                .ofNullable(confirm.getOverrides()).orElse(List.of())
                .stream()
                .collect(Collectors.toMap(
                        CsvConfirmCreateTableRequest.ColumnOverride::getColumnName,
                        o -> o,
                        (a, b) -> b));

        Set<String> pkSet = new HashSet<>(confirm.getPrimaryKeys());

        // Merge the overrides -> proposal, and create CreateColumnRequest based on the
        // proposal
        List<CreateColumnRequest> columns = scan.columns().stream().map(col -> {
            var o = overrideMap.get(col.columnName());

            String dataType = (o != null && o.getDataType() != null && !o.getDataType().isBlank())
                    ? o.getDataType()
                    : col.suggestedDataType();

            boolean nullable = (o != null && o.getNullable() != null)
                    ? o.getNullable()
                    : col.nullable();

            boolean isPk = pkSet.contains(col.columnName());

            String description = (o != null && !o.getDescription().isBlank() ? o.getDescription() : null);

            // enforce your invariant: pk => not null
            if (isPk)
                nullable = false;

            CreateColumnRequest c = new CreateColumnRequest();
            c.setColumnName(col.columnName());
            c.setDataType(dataType);
            c.setIsNullable(nullable);
            c.setIsPrimary(isPk);
            c.setDescription(description);
            return c;
        }).toList();

        CreateTableRequest req = new CreateTableRequest();
        req.setName(confirm.getTableName());
        req.setDescription(confirm.getDescription());
        req.setColumns(columns);
        return req;
    }

    private static String stripExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return (dot > 0) ? filename.substring(0, dot) : filename;
    }

    /**
     * Simple mapping for v1. Later you can centralize this in PostgresTypeRegistry.
     */
    private static String toPostgresType(InferredType t) {
        return switch (t) {
            case INTEGER -> "INTEGER";
            case LONG -> "BIGINT";
            case DOUBLE -> "DOUBLE PRECISION";
            case BOOLEAN -> "BOOLEAN";
            case TIMESTAMP -> "TIMESTAMP";
            case STRING -> "TEXT";
        };
    }
}
