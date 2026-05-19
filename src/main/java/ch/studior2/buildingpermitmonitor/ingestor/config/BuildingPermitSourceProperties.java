package ch.studior2.buildingpermitmonitor.ingestor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.building-permits")
public record BuildingPermitSourceProperties(String sourceUrl, String ingestCron) {}
