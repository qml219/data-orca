package us.sportsanalytics.backend.models.dto.workspace;

import jakarta.validation.constraints.NotBlank;

// public record WorkspaceRequest(String name, String description) {
// };

// Now that FormData is being sent from the FE, multipart/form-data;boundary=----, change from record to class - for setter-based binding instead of constructor-based binding for raw JSON -- application/json

// constructor-based binding: create 1 fully formed, immutable object != setter-based binding, immutable during construction => Flexible, support partial updates, multiple parts, streaming, etc...

// Empty constructor + Getters, setters

public class WorkspaceRequest {
    @NotBlank
    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}