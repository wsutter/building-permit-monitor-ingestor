package ch.studior2.buildingpermitmonitor.ingestor.service;

import ch.studior2.buildingpermitmonitor.contracts.event.BuildingPermitRawEvent;
import ch.studior2.buildingpermitmonitor.ingestor.kafka.BuildingPermitRawProducer;
import ch.studior2.buildingpermitmonitor.ingestor.source.CsvBuildingPermitRecordReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class BuildingPermitIngestor {

  private final BuildingPermitRawProducer producer;
  private final CsvBuildingPermitRecordReader recordReader;
  private final ExternalIdGenerator externalIdGenerator;
  private final String sourceUrl;

  public BuildingPermitIngestor(
      BuildingPermitRawProducer producer,
      CsvBuildingPermitRecordReader recordReader,
      ExternalIdGenerator externalIdGenerator,
      @Value("${app.building-permits.source-url}") String sourceUrl) {
    this.producer = producer;
    this.recordReader = recordReader;
    this.externalIdGenerator = externalIdGenerator;
    this.sourceUrl = sourceUrl;
  }

  @Scheduled(cron = "${app.building-permits.ingest-cron}")
  public void ingest() throws Exception {
    try (var inputStream = URI.create(sourceUrl).toURL().openStream();
        var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
      for (var payload : recordReader.read(reader)) {
        String externalId = externalIdGenerator.generate(payload);
        BuildingPermitRawEvent event =
            new BuildingPermitRawEvent("kt-zh", externalId, Instant.now(), payload);
        producer.send(event);
      }
    }
  }
}
