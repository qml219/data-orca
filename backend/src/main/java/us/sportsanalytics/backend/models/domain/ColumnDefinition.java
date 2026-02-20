package us.sportsanalytics.backend.models.domain;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "columns")
public class ColumnDefinition {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY) // ~ constraint fk_columns_table FOREIGN KEY (table_id) REFERENCE tables(id)
    @JoinColumn(name = "table_id", referencedColumnName = "id")
    private TableDefinition table;

    @Column(name = "column_name", nullable = false)
    @NotBlank
    private String columnName;

    @Column(name = "data_type", nullable = false)
    @NotBlank
    private String dataType;

    @Column(name = "allow_null", nullable = false)
    @NotNull
    private Boolean allowNull;

    @Column(name = "is_pk", nullable = false)
    @NotNull
    private Boolean primaryKey;

    @Column(name = "created_at", nullable = false)
    @NotNull
    private Instant createdAt;

    @Column(name = "description", nullable = true)
    private String description;

    public void validate() {
        if (primaryKey && allowNull) {
            throw new IllegalArgumentException("Primary key columns cannot be null");
        }
    }
}
