package ru.practicum.telemetry.analyzer.utils;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public class HubEventDeserializer extends AvroCommonDeserializer<HubEventAvro> {
    public HubEventDeserializer() {
        super(HubEventAvro.getClassSchema());
    }
}
