package ch.studior2.buildingpermitmonitor.ingestor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BuildingPermitIngestorApplication {

  public static void main(String[] args) {
    SpringApplication.run(BuildingPermitIngestorApplication.class, args);
  }
}
