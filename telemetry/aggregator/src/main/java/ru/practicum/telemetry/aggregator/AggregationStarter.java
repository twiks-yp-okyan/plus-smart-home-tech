package ru.practicum.telemetry.aggregator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.telemetry.aggregator.service.SnapshotStateService;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Класс AggregationStarter, ответственный за запуск агрегации данных.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    // ... объявление полей и конструктора ...
    private final Producer<Void, SpecificRecord> producer;
    private final Consumer<Void, SensorEventAvro> consumer;
    private final SnapshotStateService snapshotStateService;

    @Value("${KAFKA_TELEMETRY_SENSORS_DATA_TOPIC}")
    private String KAFKA_TELEMETRY_SENSORS_DATA_TOPIC;
    @Value("${KAFKA_TELEMETRY_SNAPSHOT_DATA_TOPIC}")
    private String KAFKA_TELEMETRY_SNAPSHOT_DATA_TOPIC;

    private final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

    /**
     * Метод для начала процесса агрегации данных.
     * Подписывается на топики для получения событий от датчиков,
     * формирует снимок их состояния и записывает в кафку.
     */
    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {

            // ... подготовка к обработке данных ...
            // ... например, подписка на топик ...
            consumer.subscribe(List.of(KAFKA_TELEMETRY_SENSORS_DATA_TOPIC));

            // Цикл обработки событий
            while (true) {
                // ... реализация цикла опроса ...
                // ... и обработка полученных данных ...
                ConsumerRecords<Void, SensorEventAvro> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);
                if (records.count() > 0) {
                    log.debug("Получено {} сообщений за один poll", records.count());
                }
                for (ConsumerRecord<Void, SensorEventAvro> record : records) {
                    log.debug("Event with: topic - {}, offset - {}, value - {}", record.topic(), record.offset(), record.value());
                    Optional<SensorsSnapshotAvro> snapshot = snapshotStateService.updateState(record.value());
                    if (snapshot.isPresent()) {
                        log.debug("Состояние снэпшота обновлено и передано для отправки в Кафку");
                        ProducerRecord<Void, SpecificRecord> producerRecord = new ProducerRecord<>(KAFKA_TELEMETRY_SNAPSHOT_DATA_TOPIC, snapshot.get());
                        producer.send(producerRecord);
                        log.debug("Обновленный снэпшот отправлен в Кафку");
                    }
                }

                consumer.commitAsync();
            }

        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {

            try {
                // Перед тем, как закрыть продюсер и консьюмер, нужно убедиться,
                // что все сообщения, лежащие в буффере, отправлены и
                // все оффсеты обработанных сообщений зафиксированы

                // здесь нужно вызвать метод продюсера для сброса данных в буффере
                producer.flush();
                log.debug("Produces flushes...");
                // здесь нужно вызвать метод консьюмера для фиксации смещений
                consumer.commitSync();
                log.debug("Consumer make commit Sync method");

            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }
}