package ch.studior2.buildingpermitmonitor.ingestor.source;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.io.StringReader;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("CsvBuildingPermitRecordReader")
class CsvBuildingPermitRecordReaderTest {

  private final CsvBuildingPermitRecordReader reader = new CsvBuildingPermitRecordReader();

  @Nested
  @DisplayName("read")
  class Read {

    @ParameterizedTest(name = "{0}")
    @MethodSource("csvInputs")
    @DisplayName("should convert CSV rows to payload maps")
    void shouldConvertCsvRowsToPayloadMaps(String csv, int expectedRows) throws Exception {
      assertThat(reader.read(new StringReader(csv))).hasSize(expectedRows);
    }

    @Test
    @DisplayName("should keep values addressable by their original CSV header names")
    void shouldKeepValuesAddressableByTheirOriginalCsvHeaderNames() throws Exception {
      String csv =
          "id,publicationNumber,Gemeinde,Bauvorhaben\n"
              + "zh-2026-0001,PUB-2026-001,Zürich,Neubau EFH\n";

      assertThat(reader.read(new StringReader(csv)))
          .singleElement()
          .satisfies(
              row ->
                  assertThat(row)
                      .containsEntry("id", "zh-2026-0001")
                      .containsEntry("publicationNumber", "PUB-2026-001")
                      .containsEntry("Gemeinde", "Zürich")
                      .containsEntry("Bauvorhaben", "Neubau EFH"));
    }

    @Test
    @DisplayName("should expose the real OGD header names as payload keys")
    void shouldExposeRealOgdHeaderNamesAsPayloadKeys() throws Exception {
      String csv =
          "id,publicationNumber,bfs_nr,municipality_name,projectDescription,"
              + "projectLocation_address_street,projectLocation_address_houseNumber,"
              + "projectLocation_address_swissZipCode,projectLocation_address_town,last_updated\n"
              + "00002982,00006183,141,Thalwil,Umbau Wohnung,"
              + "Eisenbahnstrasse,27,8800,Thalwil,2026-05-18\n";

      assertThat(reader.read(new StringReader(csv)))
          .singleElement()
          .satisfies(
              row ->
                  assertThat(row)
                      .containsEntry("municipality_name", "Thalwil")
                      .containsEntry("projectLocation_address_street", "Eisenbahnstrasse")
                      .containsEntry("projectLocation_address_swissZipCode", "8800")
                      .containsEntry("last_updated", "2026-05-18"));
    }

    @Test
    @DisplayName("should parse quoted commas as part of the field value")
    void shouldParseQuotedCommasAsPartOfFieldValue() throws Exception {
      String csv = "id,Bauvorhaben\nzh-2026-0001,\"Umbau Küche, Bad und Balkon\"\n";

      assertThat(reader.read(new StringReader(csv)))
          .singleElement()
          .satisfies(
              row -> assertThat(row).containsEntry("Bauvorhaben", "Umbau Küche, Bad und Balkon"));
    }

    static Stream<Arguments> csvInputs() {
      return Stream.of(
          arguments(
              named("single building permit row", "Gemeinde,Bauvorhaben\nZürich,Neubau EFH\n"), 1),
          arguments(
              named(
                  "two building permit rows",
                  "Gemeinde,Bauvorhaben\nZürich,Neubau EFH\nThalwil,Umbau Wohnung\n"),
              2));
    }
  }
}
