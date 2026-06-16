package ru.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Service;
import ru.practicum.telemetry.collector.service.KafkaEventProducer;
import ru.practicum.telemetry.collector.utils.EnumMapper;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;

@Service
public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {
    public ScenarioAddedEventHandler(KafkaEventProducer kafkaEventProducer) {
        super(kafkaEventProducer);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public ScenarioAddedEventAvro mapToAvro(HubEventProto event) {
        ScenarioAddedEventProto scenarioAddedEvent = event.getScenarioAdded();
        return ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAddedEvent.getName())
                .setConditions(mapConditionsToAvro(scenarioAddedEvent.getConditionList()))
                .setActions(mapActionsToAvro(scenarioAddedEvent.getActionList()))
                .build();
    }

    private ScenarioConditionAvro mapScenarioConditionToAvro(ScenarioConditionProto condition) {
        Object conditionValue = switch (condition.getValueCase()) {
            case INT_VALUE -> condition.getIntValue();
            case BOOL_VALUE -> condition.getBoolValue();
            default -> null;
        };

        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(EnumMapper.map(condition.getType(), ConditionTypeAvro.class))
                .setOperation(EnumMapper.map(condition.getOperation(), ConditionOperationAvro.class))
                .setValue(conditionValue)
                .build();
    }

    private List<ScenarioConditionAvro> mapConditionsToAvro(List<ScenarioConditionProto> conditions) {
        return conditions.stream()
                .map(this::mapScenarioConditionToAvro)
                .toList();
    }

    private DeviceActionAvro mapDeviceActionToAvro(DeviceActionProto action) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(EnumMapper.map(action.getType(), ActionTypeAvro.class))
                .setValue(action.getValue())
                .build();
    }

    private List<DeviceActionAvro> mapActionsToAvro(List<DeviceActionProto> actions) {
        return actions.stream()
                .map(this::mapDeviceActionToAvro)
                .toList();
    }
}
