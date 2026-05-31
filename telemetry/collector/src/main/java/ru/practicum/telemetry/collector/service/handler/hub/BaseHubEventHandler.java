package ru.practicum.telemetry.collector.service.handler.hub;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.telemetry.collector.model.hub.HubEvent;
import ru.practicum.telemetry.collector.model.hub.HubEventType;
import ru.practicum.telemetry.collector.service.KafkaEventProducer;
import ru.practicum.telemetry.collector.service.handler.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Slf4j
public abstract class BaseHubEventHandler<T extends SpecificRecord> implements HubEventHandler {
    private final KafkaEventProducer kafkaEventProducer;
    @Value("${KAFKA_TOPIC_NAME_HUBS_EVENTS}")
    private String KAFKA_TOPIC_NAME;

    protected BaseHubEventHandler(KafkaEventProducer kafkaEventProducer) {
        this.kafkaEventProducer = kafkaEventProducer;
    }

    @Override
    public abstract HubEventType getMessageType();

    public abstract T mapToAvro(HubEvent event);

    @Override
    public void handle(HubEvent event) {
        T eventTypeAvroData = mapToAvro(event);
        log.debug("Mapping to Avro for {} event type: success. Data: {}", getMessageType(), eventTypeAvroData);

        HubEventAvro eventAvro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(eventTypeAvroData)
                .build();
        log.debug("Send mapped to Avro event to KafkaProducerService with data: {}", eventAvro);

        kafkaEventProducer.send(KAFKA_TOPIC_NAME, eventAvro);
    }
}
