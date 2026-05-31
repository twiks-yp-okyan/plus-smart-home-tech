package ru.practicum.telemetry.collector.service.handler.sensor;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.practicum.telemetry.collector.model.sensor.SensorEventType;
import ru.practicum.telemetry.collector.service.KafkaEventProducer;
import ru.practicum.telemetry.collector.service.handler.SensorEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Slf4j
public abstract class BaseSensorEventHandler<T extends SpecificRecord> implements SensorEventHandler {
    private final KafkaEventProducer kafkaEventProducer;
    @Value("${KAFKA_TOPIC_NAME_SENSORS_EVENTS}")
    private String KAFKA_TOPIC_NAME;

    protected BaseSensorEventHandler(KafkaEventProducer kafkaEventProducer) {
        this.kafkaEventProducer = kafkaEventProducer;
    }

    @Override
    public abstract SensorEventType getMessageType();

    public abstract T mapToAvro(SensorEvent event);

    @Override
    public void handle(SensorEvent event) {
        T eventTypeAvroData = mapToAvro(event);
        log.debug("Mapping to Avro for {} event type: success. Data: {}", getMessageType(), eventTypeAvroData);

        SensorEventAvro eventAvro = SensorEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setId(event.getId())
                .setTimestamp(event.getTimestamp())
                .setPayload(eventTypeAvroData)
                .build();
        log.debug("Send mapped to Avro event to KafkaProducerService with data: {}", eventAvro);

        kafkaEventProducer.send(KAFKA_TOPIC_NAME, eventAvro);
    }
}
