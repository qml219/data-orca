package us.sportsanalytics.backend.models.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import us.sportsanalytics.backend.models.domain.roles.Role;

@Table("users")
public record User(
        @Id UUID id,
        String email,
        String username,
        String passwordHash,
        Role role,
        LocalDateTime createdAt) {
};
