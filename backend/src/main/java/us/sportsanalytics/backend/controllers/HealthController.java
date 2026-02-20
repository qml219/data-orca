package us.sportsanalytics.backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import us.sportsanalytics.backend.models.domain.HealthStatus;
import us.sportsanalytics.backend.services.HealthService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/health")
public class HealthController {

    private static final Logger log = LoggerFactory.getLogger(HealthController.class);

    private final HealthService healthService;

    // @Lombok's @RequiredArgsConstructor annotation avoids writing simple
    // contructors
    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping
    public HealthStatus getHealth() {
        log.info("Received GET /health request");
        return healthService.check();
    }

}
