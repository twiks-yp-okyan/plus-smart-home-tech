package ru.practicum.telemetry.analyzer.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.telemetry.analyzer.model.Sensor;
import ru.practicum.telemetry.analyzer.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeviceAddedEventHandler implements HubEventHandler {
    private final SensorRepository repository;

    @Override
    public String getPayloadType() {
        return DeviceAddedEventAvro.class.getSimpleName();
    }

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        DeviceAddedEventAvro deviceEvent = (DeviceAddedEventAvro) event.getPayload();
        log.debug("Save new device with id - {} for hub - {}", deviceEvent.getId(), event.getHubId());
        repository.save(mapToSensor(event.getHubId(), deviceEvent));
    }

    private Sensor mapToSensor(String hubId, DeviceAddedEventAvro deviceEvent) {
        return Sensor.builder()
                .id(deviceEvent.getId())
                .hubId(hubId)
                .build();
    }
}
