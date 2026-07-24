package ru.practicum.telemetry.analyzer.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.telemetry.analyzer.exception.NotFoundException;
import ru.practicum.telemetry.analyzer.model.Sensor;
import ru.practicum.telemetry.analyzer.repository.SensorRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeviceRemovedEventHandler implements HubEventHandler {
    private final SensorRepository repository;

    @Override
    public String getPayloadType() {
        return DeviceRemovedEventAvro.class.getSimpleName();
    }

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        DeviceRemovedEventAvro deviceRemoved = (DeviceRemovedEventAvro) event.getPayload();
        log.debug("Remove device with id - {} from hub - {}", deviceRemoved.getId(), event.getHubId());
        Sensor existingSensor = repository.findById(deviceRemoved.getId())
                .orElseThrow(
                        () -> new NotFoundException(
                                String.format("Устройство с id = %s не найдено в хабе с id = %s",
                                        deviceRemoved.getId(), event.getHubId())
                        )
                );
        repository.delete(existingSensor);
    }
}
