package us.sportsanalytics.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import us.sportsanalytics.backend.models.domain.HealthStatus;

import java.time.LocalDateTime;
import javax.sql.DataSource;

@Service
public class HealthService {

    private final static Logger log = LoggerFactory.getLogger(HealthService.class);

    private final DataSource dataSource;

    public HealthService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public HealthStatus check() {
        log.info("Performing health check");
        String dbStatus = checkDatabase();
        return new HealthStatus("UP", dbStatus, LocalDateTime.now());
    }

    private String checkDatabase() {
        try (var conn = dataSource.getConnection()) {
            log.info("Database Connection Established");
            return "UP";
        } catch (Exception e) {
            log.info("Database Connection Failed", e);
            return "DOWN";
        }
    }
}
