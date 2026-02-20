package us.sportsanalytics.backend.services;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class PostgresTypeRegistry {

    private final JdbcTemplate jdbcTemplate;

    public PostgresTypeRegistry(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean validateDataType(String dataType) {

        String baseType = extractBaseType(dataType);

        return jdbcTemplate.queryForObject(
                """
                            SELECT EXISTS (SELECT 1 FROM pg_type where typname = ?)
                        """, Boolean.class, baseType);

    }

    // VARCHAR(20) -> varchar
    public String extractBaseType(String dataType) {
        return dataType.toLowerCase().split("\\(")[0].trim();
    }

}
