package us.sportsanalytics.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "app.storage")
@Getter
@Setter
public class StorageProperties {

    private String bucket;
    private long presignedURLDuration;
}
