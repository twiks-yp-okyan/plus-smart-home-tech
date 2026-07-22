package ru.practicum.telemetry.analyzer.service.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Component;
import ru.practicum.telemetry.analyzer.service.handler.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HubEventProcessor implements Runnable {
    private final Map<String, KafkaConsumer<Void, SpecificRecord>> consumers;
    private final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);
    private final Map<String, HubEventHandler> hubEventHandlers;

    public HubEventProcessor(
            Map<String, KafkaConsumer<Void, SpecificRecord>> consumers,
            List<HubEventHandler> hubEventHandlers
    ) {
        this.consumers = consumers;
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getPayloadType, Function.identity()));
    }

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
                    log.debug("Выбираем обработчик для события - {}", hubEventAvro);
                    HubEventHandler handler = chooseHandler(hubEventAvro);
                    log.debug("Обработчик выбран - это {}", handler.getPayloadType());
                    handler.handle(hubEventAvro);
                    log.debug("Событие - {} обработано обработчиком - {}", hubEventAvro, handler.getPayloadType());
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

    private HubEventHandler chooseHandler(HubEventAvro event) {
        HubEventHandler handler = hubEventHandlers.get(event.getPayload().getClass().getSimpleName());
        if (handler == null) {
            throw new IllegalArgumentException("Нет обработчика для события " + event);
        }
        return handler;
    }
}
