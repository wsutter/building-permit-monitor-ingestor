package ch.studior2.buildingpermitmonitor.ingestor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaRepositories(basePackages = "ch.studior2.buildingpermitmonitor.persistence.repository")
@EntityScan(basePackages = "ch.studior2.buildingpermitmonitor.persistence.entity")
@SpringBootApplication
public class BuildingPermitIngestorApplication {

  public static void main(String[] args) {
    SpringApplication.run(BuildingPermitIngestorApplication.class, args);
  }
}
