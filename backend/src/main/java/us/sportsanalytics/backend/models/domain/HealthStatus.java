package us.sportsanalytics.backend.models.domain;

import java.time.LocalDateTime;

public record HealthStatus(
        String status,
        String database,
        LocalDateTime timestamp) {
}