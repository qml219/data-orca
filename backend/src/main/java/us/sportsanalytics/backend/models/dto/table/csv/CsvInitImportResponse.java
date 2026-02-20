package us.sportsanalytics.backend.models.dto.table.csv;

import java.util.UUID;

import lombok.Data;

public record CsvInitImportResponse(
                UUID uploadSessionId,
                String uploadURL) {
}
