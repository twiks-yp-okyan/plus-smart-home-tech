package ru.practicum.telemetry.collector.service.handler.hub;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.telemetry.collector.model.hub.HubEvent;
import ru.practicum.telemetry.collector.model.hub.HubEventType;
import ru.practicum.telemetry.collector.service.KafkaEventProducer;
import ru.practicum.telemetry.collector.service.handler.HubEventHandler;

@Slf4j
public abstract class BaseHubEventHandler<T extends SpecificRecord> implements HubEventHandler {
    private final KafkaEventProducer kafkaEventProducer;
    @Value("${KAFKA_TOPIC_NAME_HUBS_EVENTS}")
    private String KAFKA_TOPIC_NAME;

    protected BaseHubEventHandler(KafkaEventProducer kafkaEventProducer) {
        this.kafkaEventProducer = kafkaEventProducer;
    }

    public abstract HubEventType getMessageType();

    public abstract T mapToAvro(HubEvent event);

    public void handle(HubEvent event) {
        T avroData = mapToAvro(event);
        log.debug("Send mapped to Avro event to KafkaProducerService with data: {}", avroData);
        kafkaEventProducer.send(KAFKA_TOPIC_NAME, avroData);
    }
}
