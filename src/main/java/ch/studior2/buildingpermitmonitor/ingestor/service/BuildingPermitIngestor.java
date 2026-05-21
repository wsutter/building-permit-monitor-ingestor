package ch.studior2.buildingpermitmonitor.ingestor.service;

import static java.util.stream.Collectors.toMap;

import ch.studior2.buildingpermitmonitor.contracts.event.BuildingPermitRawEvent;
import ch.studior2.buildingpermitmonitor.ingestor.kafka.BuildingPermitRawProducer;
import ch.studior2.buildingpermitmonitor.ingestor.source.CsvBuildingPermitRecordReader;
import ch.studior2.buildingpermitmonitor.persistence.api.BuildingPermitRawEventRegistry;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

@Service
public class BuildingPermitIngestor {

  private final BuildingPermitRawProducer producer;
  private final CsvBuildingPermitRecordReader recordReader;
  private final BuildingPermitRawEventRegistry rawEventRegistry;
  private final JsonMapper jsonMapper;
  private final String sourceUrl;

  private static final Logger LOG = LoggerFactory.getLogger(BuildingPermitIngestor.class);

  public BuildingPermitIngestor(
      BuildingPermitRawProducer producer,
      CsvBuildingPermitRecordReader recordReader,
      BuildingPermitRawEventRegistry rawEventRegistry,
      JsonMapper jsonMapper,
      @Value("${app.building-permits.source-url}") String sourceUrl) {
    this.producer = producer;
    this.recordReader = recordReader;
    this.rawEventRegistry = rawEventRegistry;
    this.jsonMapper = jsonMapper;
    this.sourceUrl = sourceUrl;
  }

  @Scheduled(cron = "${app.building-permits.ingest-cron}")
  public void ingest() throws Exception {
    try (var inputStream = URI.create(sourceUrl).toURL().openStream();
        var reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

      for (var payload : recordReader.read(reader)) {
        BuildingPermitRawEvent event =
            jsonMapper.convertValue(prune(payload), BuildingPermitRawEvent.class);

        if (rawEventRegistry.registerIfNew(event.id(), event.publicationNumber())) {
          producer.send(event);
        } else {
          LOG.info("Skipping duplicate raw event: {}:{}", event.id(), event.publicationNumber());
        }
      }
    }
  }

  private static Map<String, String> prune(Map<String, String> payload) {
    return payload.entrySet().stream()
        .filter(entry -> entry.getValue() != null && !entry.getValue().isBlank())
        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
  }
}
