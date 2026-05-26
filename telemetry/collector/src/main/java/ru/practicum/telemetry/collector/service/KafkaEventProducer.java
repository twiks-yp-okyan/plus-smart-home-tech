package ru.practicum.telemetry.collector.service;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;

@Service
public class KafkaEventProducer {
    private final Producer<Void, SpecificRecord> producer;

    public KafkaEventProducer(Producer<Void, SpecificRecord> producer) {
        this.producer = producer;
    }

    public void send(String topicName, SpecificRecord payload) {
        ProducerRecord<Void, SpecificRecord> producerRecord = new ProducerRecord<>(topicName, payload);
        producer.send(producerRecord);
    }
}
