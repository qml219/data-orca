package us.sportsanalytics.backend.services.persistence.csv;

public enum InferredType {
    INTEGER, LONG, DOUBLE, BOOLEAN, TIMESTAMP, STRING;

    public static InferredType widen(InferredType a, InferredType b) {
        if (a == b) {
            return a;
        }

        if ((a == INTEGER && b == LONG) || (a == LONG && b == INTEGER)) {
            return LONG;
        }

        if ((a == INTEGER && b == DOUBLE) || (a == DOUBLE && b == INTEGER)) {
            return DOUBLE;
        }

        if ((a == DOUBLE && b == LONG) || (a == LONG && b == DOUBLE)) {
            return DOUBLE;
        }

        // If it's a mix of numeric values and strings/timestamp, then STRING is the
        // narrowest type that fits all the values.
        return STRING;
    }
}
