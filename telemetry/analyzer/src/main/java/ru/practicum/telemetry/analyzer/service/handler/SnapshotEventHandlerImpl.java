package ru.practicum.telemetry.analyzer.service.handler;

import com.google.protobuf.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.telemetry.analyzer.model.Action;
import ru.practicum.telemetry.analyzer.service.ScenarioService;
import ru.practicum.telemetry.analyzer.utils.EnumMapper;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SnapshotEventHandlerImpl implements SnapshotEventHandler {
    private final ScenarioService scenarioService;

    @Override
    public List<DeviceActionRequest> handle(SensorsSnapshotAvro event) {
        log.debug("Начало обработки снэпшота для хаба - {}", event.getHubId());
        List<String> successScenarios = scenarioService.checkHubScenarios(event.getHubId(), event);
        Map<String, Map<String, Action>> successScenariosAction = successScenarios.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        scenarioName -> scenarioService.scenarioActions(event.getHubId(), scenarioName)
                ));
        return successScenariosAction.entrySet().stream()
                .flatMap(
                        scenarioSensorAction -> scenarioSensorAction.getValue().entrySet().stream()
                                .map(scenarioAction -> buildRequest(
                                        event.getHubId(),
                                        scenarioSensorAction.getKey(),
                                        scenarioAction.getKey(),
                                        scenarioAction.getValue()
                                ))
                )
                .toList();
    }

    private DeviceActionRequest buildRequest(String hubId, String scenarioName, String sensorId, Action action) {
        return DeviceActionRequest.newBuilder()
                .setHubId(hubId)
                .setScenarioName(scenarioName)
                .setAction(buildAction(sensorId, action))
                .setTimestamp(Timestamp.newBuilder()
                        .setSeconds(Instant.now().getEpochSecond())
                        .build())
                .build();
    }

    private DeviceActionProto buildAction(String sensorId, Action action) {
        return DeviceActionProto.newBuilder()
                .setSensorId(sensorId)
                .setType(EnumMapper.map(action.getType(), ActionTypeProto.class))
                .build();
    }
}
