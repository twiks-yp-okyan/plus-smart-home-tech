package ru.practicum.telemetry.collector.service.handler.hub;

import org.apache.avro.specific.SpecificRecord;
import ru.practicum.telemetry.collector.model.hub.HubEvent;
import ru.practicum.telemetry.collector.model.hub.HubEventType;
import ru.practicum.telemetry.collector.service.KafkaEventProducer;
import ru.practicum.telemetry.collector.service.handler.HubEventHandler;

//@RequiredArgsConstructor
public abstract class BaseHubEventHandler<T extends SpecificRecord> implements HubEventHandler {
    private final KafkaEventProducer kafkaEventProducer;
//    @Value("${KAFKA_TOPIC_NAME_HUBS_EVENTS}")
//    private final String KAFKA_TOPIC_NAME;

    protected BaseHubEventHandler(KafkaEventProducer kafkaEventProducer) {
        this.kafkaEventProducer = kafkaEventProducer;
    }

    public abstract HubEventType getMessageType();

    public abstract T mapToAvro(HubEvent event);

    public void handle(HubEvent event) {
        T avroData = mapToAvro(event);
        kafkaEventProducer.send("telemetry.hubs.v1", avroData);
    }
}
