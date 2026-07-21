package ru.practicum.telemetry.analyzer.utils;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public class SnapshotEventDeserializer extends AvroCommonDeserializer<SensorsSnapshotAvro> {
    public SnapshotEventDeserializer() {
        super(SensorsSnapshotAvro.getClassSchema());
    }
}
