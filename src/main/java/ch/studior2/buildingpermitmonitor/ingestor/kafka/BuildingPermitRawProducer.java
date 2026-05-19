package ch.studior2.buildingpermitmonitor.ingestor.kafka;

import ch.studior2.buildingpermitmonitor.contracts.event.BuildingPermitRawEvent;
import ch.studior2.buildingpermitmonitor.contracts.topic.KafkaTopics;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class BuildingPermitRawProducer {

  private final KafkaTemplate<String, BuildingPermitRawEvent> kafkaTemplate;

  public BuildingPermitRawProducer(KafkaTemplate<String, BuildingPermitRawEvent> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void send(BuildingPermitRawEvent event) {
    kafkaTemplate.send(KafkaTopics.RAW, event.externalId(), event);
  }
}
