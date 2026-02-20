package us.sportsanalytics.backend.models.domain;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
// import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// @Data
@Getter
@Setter
@Entity
@NoArgsConstructor
// table name has to be unique within a schema (workspace)
@Table(name = "tables", uniqueConstraints = @UniqueConstraint(columnNames = { "schema_name", "table_name" }))
public class TableDefinition {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "table_name", nullable = false) // Affect schema when table is created with
    // spring.jpa.hibernate.ddl-auto=[none/validate]. If table already exists in db,
    // it does not add a NOT NULL constraint if it wasn't there from the start.
    @NotBlank
    private String tableName;

    @Column(name = "schema_name", nullable = false)
    @NotBlank
    private String schemaName;

    @Column(name = "created_at", nullable = false)
    @NotNull
    private Instant createdAt;

    @Column(name = "description")
    private String description;

}
