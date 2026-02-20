package us.sportsanalytics.backend.models.domain;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;

@Getter
@Table(name = "workspaces")
public class Workspace {
    @Id
    private final UUID id;
    private final String schemaName; // Schema name doesn't change - class invariant
    private String name;
    private Instant createdAt;
    private String description;

    public Workspace(UUID id, String name, String schemaName, String description) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Workspace name required");
        }
        if (schemaName == null || schemaName.isBlank()) {
            throw new IllegalArgumentException("Schema name required");
        }
        this.id = id;
        this.name = name;
        this.schemaName = schemaName;
        this.createdAt = Instant.now();
        this.description = description;
    }
}
