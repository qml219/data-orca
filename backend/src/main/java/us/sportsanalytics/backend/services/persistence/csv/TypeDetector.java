package us.sportsanalytics.backend.services.persistence.csv;

import java.time.Instant;

public class TypeDetector {

    public static InferredType inferType(String raw) {

        if (raw == null || raw.isBlank()) {
            return InferredType.STRING;
        }

        String val = raw.trim();

        if (isInteger(val))
            return InferredType.INTEGER;
        if (isLong(val))
            return InferredType.LONG;
        if (isDouble(val))
            return InferredType.DOUBLE;
        if (isBoolean(val))
            return InferredType.BOOLEAN;
        if (isTimestamp(val))
            return InferredType.TIMESTAMP;

        return InferredType.STRING;

    }

    private static boolean isInteger(String raw) {
        try {
            Integer.parseInt(raw);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isDouble(String raw) {
        try {
            Double.parseDouble(raw);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isLong(String raw) {
        try {
            Long.parseLong(raw);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isBoolean(String raw) {
        return raw.equalsIgnoreCase("true") || raw.equalsIgnoreCase("false");
    }

    private static boolean isTimestamp(String raw) {
        try {
            Instant.parse(raw);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
