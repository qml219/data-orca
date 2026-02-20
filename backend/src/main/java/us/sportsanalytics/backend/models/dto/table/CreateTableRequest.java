package us.sportsanalytics.backend.models.dto.table;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Because @RequestBody has Jackson deserializing request body and builds dtos on a constructor basis, it needs an AllArgsContrusctor -- for final and NonNull fields.
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CreateTableRequest {

    private String name;

    private String description;

    private List<CreateColumnRequest> columns;

}
