package ch.studior2.buildingpermitmonitor.ingestor.config;

import ch.studior2.buildingpermitmonitor.persistence.config.BuildingPermitPersistenceConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(BuildingPermitPersistenceConfig.class)
@Configuration
public class BuildingPermitIngestorConfig {}
