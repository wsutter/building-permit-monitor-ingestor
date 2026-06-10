# Ingestor Module

## Purpose

The Ingestor is the entry point of the Building Permit Monitor pipeline.

It reads raw building permit data from external sources and publishes events to Kafka.

## Responsibilities

### Data Import

The service imports permit data from CSV files.

Supported operations:

* Read source file
* Parse records
* Validate mandatory fields
* Create raw events

### Event Publishing

For every imported record, a `BuildingPermitRawEvent` is created and published to:

```text
building-permit.raw
```

### Duplicate Prevention

The module uses the Persistence API to check whether a permit has already been processed.

Business key:

```text
externalId = id + ":" + publicationNumber
```

Only new permits are published.

## Event Flow

```text
CSV File
    ↓
BuildingPermitRawEvent
    ↓
building-permit.raw
```

## Technologies / Frameworks

* Java 25
* Spring Boot 4
* Spring Kafka
* OpenCSV

## Startup

```bash
mvn spring-boot:run -pl ingestor
```
