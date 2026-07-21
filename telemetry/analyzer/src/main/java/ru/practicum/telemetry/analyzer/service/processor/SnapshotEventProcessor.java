package ru.practicum.telemetry.analyzer.service.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class SnapshotEventProcessor {
    private final Map<String, KafkaConsumer<Void, SpecificRecord>> consumers;
    private final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

    public void start() {
        KafkaConsumer<Void, SpecificRecord> consumer = consumers.get("snapshots");

        try {
            while (true) {
                ConsumerRecords<Void, SpecificRecord> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);
                if (!records.isEmpty()) {
                    log.debug("Получено {} сообщений за один poll из топика - telemetry.snapshots.v1", records.count());
                }
                for (ConsumerRecord<Void, SpecificRecord> record : records) {
                    log.debug("SNAPSHOT Event with: offset - {}, value - {}", record.offset(), record.value());

                    SensorsSnapshotAvro sensorsSnapshotAvro = (SensorsSnapshotAvro) record.value();
                    // TODO - обработка снапшота
                }
                consumer.commitAsync();
            }
        } catch (Exception e) {
            log.error("Ошибка во время обработки снапшота", e);
        } finally {
            try {
                consumer.commitSync();
                log.debug("Snapshot-Consumer make commit Sync method");
            } finally {
                log.info("Закрываем консьюмер-snapshot");
                consumer.close();
            }
        }
    }
}
