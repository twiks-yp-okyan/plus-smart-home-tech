package ru.practicum.telemetry.analyzer.service.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class HubEventProcessor implements Runnable {
    private final Map<String, KafkaConsumer<Void, SpecificRecord>> consumers;
    private final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

    @Override
    public void run() {
        KafkaConsumer<Void, SpecificRecord> consumer = consumers.get("hubs");

        try {
            while (true) {
                ConsumerRecords<Void, SpecificRecord> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);
                if (!records.isEmpty()) {
                    log.debug("Получено {} сообщений за один poll из топика - telemetry.hubs.v1", records.count());
                }
                for (ConsumerRecord<Void, SpecificRecord> record : records) {
                    log.debug("HUB Event with: offset - {}, value - {}", record.offset(), record.value());

                    HubEventAvro hubEventAvro = (HubEventAvro) record.value();
                    // TODO - обработка событий хаба
                }
                consumer.commitAsync();
            }
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от хаба", e);
        } finally {
            try {
                consumer.commitSync();
                log.debug("Hub-Consumer make commit Sync method");
            } finally {
                log.info("Закрываем консьюмер-hub");
                consumer.close();
            }
        }
    }
}
