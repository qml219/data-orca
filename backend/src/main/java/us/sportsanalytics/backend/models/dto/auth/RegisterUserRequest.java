package us.sportsanalytics.backend.models.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @Email @NotBlank String email,
        @NotBlank String username,
        @NotBlank @Size(min = 8) String password) {
}
