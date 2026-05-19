module ch.studior2.buildingpermitmonitor.ingestor {
  requires spring.boot;
  requires spring.boot.autoconfigure;
  requires spring.context;
  requires spring.beans;
  requires spring.kafka;
  requires kafka.clients;
  requires tools.jackson.databind;
  requires org.apache.commons.csv;
  requires ch.studior2.buildingpermitmonitor.contracts;

  opens ch.studior2.buildingpermitmonitor.ingestor to
      spring.core,
      spring.beans,
      spring.context;
  opens ch.studior2.buildingpermitmonitor.ingestor.config to
      spring.core,
      spring.beans,
      spring.context;
  opens ch.studior2.buildingpermitmonitor.ingestor.kafka to
      spring.core,
      spring.beans,
      spring.context;
  opens ch.studior2.buildingpermitmonitor.ingestor.service to
      spring.core,
      spring.beans,
      spring.context;
  opens ch.studior2.buildingpermitmonitor.ingestor.source to
      spring.core,
      spring.beans,
      spring.context;
}
