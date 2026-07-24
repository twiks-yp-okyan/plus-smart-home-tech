package ru.practicum.telemetry.analyzer.service.handler;

import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.List;

public interface SnapshotEventHandler {
    List<DeviceActionRequest> handle(SensorsSnapshotAvro event);
}
