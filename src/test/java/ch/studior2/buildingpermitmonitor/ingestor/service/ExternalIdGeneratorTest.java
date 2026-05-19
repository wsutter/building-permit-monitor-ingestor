package ch.studior2.buildingpermitmonitor.ingestor.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("ExternalIdGenerator")
class ExternalIdGeneratorTest {

  private final ExternalIdGenerator generator = new ExternalIdGenerator();

  @Nested
  @DisplayName("generate")
  class Generate {

    @ParameterizedTest(name = "{0}")
    @MethodSource("payloads")
    @DisplayName("should create deterministic IDs independent of map insertion order")
    void shouldCreateDeterministicIds(Map<String, String> first, Map<String, String> second) {
      assertThat(generator.generate(first)).isEqualTo(generator.generate(second));
    }

    static Stream<Arguments> payloads() {
      return Stream.of(
          arguments(
              named(
                  "same logical payload with different insertion order",
                  Map.of("Gemeinde", "Zürich", "Adresse", "Limmatstrasse 1")),
              Map.of("Adresse", "Limmatstrasse 1", "Gemeinde", "Zürich")));
    }
  }
}
