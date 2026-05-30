package us.sportsanalytics.backend.models.dto.table;

import java.util.UUID;

public record ScanRequest(
        UUID uploadSessionId,
        String tableName) {
}