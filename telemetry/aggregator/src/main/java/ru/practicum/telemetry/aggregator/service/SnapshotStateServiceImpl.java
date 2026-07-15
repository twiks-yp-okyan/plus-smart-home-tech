package ru.practicum.telemetry.aggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class SnapshotStateServiceImpl implements SnapshotStateService {
    private final Map<String, SensorsSnapshotAvro> snapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        // проверка наличия снэпшшота для hubId
        SensorsSnapshotAvro currentSnapshot = snapshots.get(event.getHubId());
        log.debug("Current snapshot - {}", currentSnapshot);

        if (currentSnapshot == null) {
            log.debug("Формируемм новый state нового снапшота для хаба - {}", event.getHubId());
            Map<String, SensorStateAvro> sensorsState = new HashMap<>();
            sensorsState.put(
                    event.getId(),
                    SensorStateAvro.newBuilder()
                            .setTimestamp(event.getTimestamp())
                            .setData(event.getPayload())
                            .build()
            );
            log.debug("Формируемм новый snapshot для хаба - {}", event.getHubId());
            currentSnapshot = SensorsSnapshotAvro.newBuilder()
                    .setHubId(event.getHubId())
                    .setTimestamp(event.getTimestamp())
                    .setSensorsState(sensorsState)
                    .build();

        } else {
            SensorStateAvro oldState = currentSnapshot.getSensorsState().get(event.getId());
            log.debug("SensorOldState - {}", oldState);

            if (oldState != null && (oldState.getTimestamp().isAfter(event.getTimestamp())
                    || oldState.getData().equals(event.getPayload()))
            ) {
                log.debug("Снапшот не изменился");
                return Optional.empty();
            }
            log.debug("Снапшот изменился");

            SensorStateAvro newState = SensorStateAvro.newBuilder()
                    .setTimestamp(event.getTimestamp())
                    .setData(event.getPayload())
                    .build();
            log.debug("SensorNewState - {}", newState);

            currentSnapshot.getSensorsState().put(event.getId(), newState);
            currentSnapshot.setTimestamp(event.getTimestamp());
        }

        snapshots.put(event.getHubId(), currentSnapshot);

        return Optional.of(currentSnapshot);
    }

}
