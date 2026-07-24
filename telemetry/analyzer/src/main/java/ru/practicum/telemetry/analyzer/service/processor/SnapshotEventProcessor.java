package ru.practicum.telemetry.analyzer.service.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Component;
import ru.practicum.telemetry.analyzer.service.ActionSender;
import ru.practicum.telemetry.analyzer.service.handler.SnapshotEventHandler;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class SnapshotEventProcessor {
    private final Map<String, KafkaConsumer<Void, SpecificRecord>> consumers;
    private final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);
    private final SnapshotEventHandler snapshotEventHandler;
    private final ActionSender actionSender;

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
                    List<DeviceActionRequest> requests = snapshotEventHandler.handle(sensorsSnapshotAvro);
                    requests.stream()
                            .peek(req -> log.debug("Отправка запросов в gRPC - {}", req))
                            .forEach(actionSender::sendAction);
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
