package ch.studior2.buildingpermitmonitor.ingestor.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.studior2.buildingpermitmonitor.contracts.event.BuildingPermitRawEvent;
import ch.studior2.buildingpermitmonitor.ingestor.kafka.BuildingPermitRawProducer;
import ch.studior2.buildingpermitmonitor.ingestor.source.CsvBuildingPermitRecordReader;
import ch.studior2.buildingpermitmonitor.persistence.api.BuildingPermitRawEventRegistry;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import tools.jackson.databind.json.JsonMapper;

@DisplayName("BuildingPermitIngestor")
class BuildingPermitIngestorTest {

  private final BuildingPermitRawProducer producer = mock(BuildingPermitRawProducer.class);
  private final CsvBuildingPermitRecordReader recordReader =
      mock(CsvBuildingPermitRecordReader.class);
  private final BuildingPermitRawEventRegistry rawEventRegistry =
      mock(BuildingPermitRawEventRegistry.class);
  private final JsonMapper jsonMapper = mock(JsonMapper.class);

  @TempDir private Path tempDir;

  @Nested
  @DisplayName("ingest")
  class Ingest {

    @Test
    @DisplayName(
        "should prune blank payload values, register the business key and publish new events")
    void shouldPruneBlankPayloadValuesRegisterBusinessKeyAndPublishNewEvents() throws Exception {
      Path csvFile = writeCsvFile();
      BuildingPermitRawEvent event = rawEvent("zh-2026-0001", "PUB-2026-001");
      when(recordReader.read(any(Reader.class)))
          .thenReturn(
              List.of(
                  Map.of(
                      "id", "zh-2026-0001",
                      "publicationNumber", "PUB-2026-001",
                      "municipality", "Zürich",
                      "emptyValue", "",
                      "blankValue", "   ")));
      when(jsonMapper.convertValue(anyMap(), eq(BuildingPermitRawEvent.class))).thenReturn(event);
      when(rawEventRegistry.registerIfNew("zh-2026-0001", "PUB-2026-001")).thenReturn(true);

      ingestor(csvFile).ingest();

      ArgumentCaptor<Map<String, String>> payloadCaptor = ArgumentCaptor.forClass(Map.class);
      verify(jsonMapper).convertValue(payloadCaptor.capture(), eq(BuildingPermitRawEvent.class));
      assertThat(payloadCaptor.getValue())
          .containsEntry("id", "zh-2026-0001")
          .containsEntry("publicationNumber", "PUB-2026-001")
          .containsEntry("municipality", "Zürich")
          .doesNotContainKeys("emptyValue", "blankValue");

      verify(rawEventRegistry).registerIfNew("zh-2026-0001", "PUB-2026-001");
      verify(producer).send(event);
    }

    @Test
    @DisplayName("should skip publishing when the raw event is already registered")
    void shouldSkipPublishingWhenRawEventIsAlreadyRegistered() throws Exception {
      Path csvFile = writeCsvFile();
      BuildingPermitRawEvent duplicateEvent = rawEvent("zh-2026-0001", "PUB-2026-001");
      when(recordReader.read(any(Reader.class)))
          .thenReturn(List.of(Map.of("id", "zh-2026-0001", "publicationNumber", "PUB-2026-001")));
      when(jsonMapper.convertValue(anyMap(), eq(BuildingPermitRawEvent.class)))
          .thenReturn(duplicateEvent);
      when(rawEventRegistry.registerIfNew("zh-2026-0001", "PUB-2026-001")).thenReturn(false);

      ingestor(csvFile).ingest();

      verify(rawEventRegistry).registerIfNew("zh-2026-0001", "PUB-2026-001");
      verify(producer, never()).send(duplicateEvent);
    }
  }

  private BuildingPermitIngestor ingestor(Path csvFile) {
    return new BuildingPermitIngestor(
        producer, recordReader, rawEventRegistry, jsonMapper, csvFile.toUri().toString());
  }

  private Path writeCsvFile() throws Exception {
    Path csvFile = tempDir.resolve("building-permits.csv");
    Files.writeString(csvFile, "id,publicationNumber\nignored,ignored\n", StandardCharsets.UTF_8);
    return csvFile;
  }

  private static BuildingPermitRawEvent rawEvent(String id, String publicationNumber) {
    BuildingPermitRawEvent event = mock(BuildingPermitRawEvent.class);
    when(event.id()).thenReturn(id);
    when(event.publicationNumber()).thenReturn(publicationNumber);
    return event;
  }
}
