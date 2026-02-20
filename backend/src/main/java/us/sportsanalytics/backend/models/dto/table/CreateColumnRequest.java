package us.sportsanalytics.backend.models.dto.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CreateColumnRequest {

    private String columnName;

    private String dataType;

    private Boolean isPrimary;

    private Boolean isNullable;

    private String description;
}
