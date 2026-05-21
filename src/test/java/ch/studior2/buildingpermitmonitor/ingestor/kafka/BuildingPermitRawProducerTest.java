package ch.studior2.buildingpermitmonitor.ingestor.kafka;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.studior2.buildingpermitmonitor.contracts.event.BuildingPermitRawEvent;
import ch.studior2.buildingpermitmonitor.contracts.topic.KafkaTopics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

@DisplayName("BuildingPermitRawProducer")
class BuildingPermitRawProducerTest {

  private final KafkaTemplate<String, BuildingPermitRawEvent> kafkaTemplate =
      mock(KafkaTemplate.class);
  private final BuildingPermitRawProducer producer = new BuildingPermitRawProducer(kafkaTemplate);

  @Nested
  @DisplayName("send")
  class Send {

    @Test
    @DisplayName("should publish raw event to raw topic using external id as Kafka key")
    void shouldPublishRawEventToRawTopicUsingExternalIdAsKafkaKey() {
      BuildingPermitRawEvent event = mock(BuildingPermitRawEvent.class);
      when(event.externalId()).thenReturn("zh-2026-0001");

      producer.send(event);

      verify(kafkaTemplate).send(KafkaTopics.RAW, "zh-2026-0001", event);
    }
  }
}
