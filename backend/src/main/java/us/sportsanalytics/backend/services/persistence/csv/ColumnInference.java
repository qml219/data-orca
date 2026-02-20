package us.sportsanalytics.backend.services.persistence.csv;

public class ColumnInference {
    private InferredType type;
    private boolean nullable;

    public ColumnInference() {
        this.type = InferredType.INTEGER;
        this.nullable = false;
    }

    public void observe(String raw) {

        if (raw == null || raw.isBlank()) {
            this.nullable = true;
            return;
        }

        InferredType detectedType = TypeDetector.inferType(raw);
        this.type = InferredType.widen(type, detectedType);

    }

    public InferredType getType() {
        return this.type;
    }

    public boolean getNullable() {
        return this.nullable;
    }
}
