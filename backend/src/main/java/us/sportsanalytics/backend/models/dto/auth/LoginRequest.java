package us.sportsanalytics.backend.models.dto.auth;

public record LoginRequest(
        String identifier,
        String password) {
};
