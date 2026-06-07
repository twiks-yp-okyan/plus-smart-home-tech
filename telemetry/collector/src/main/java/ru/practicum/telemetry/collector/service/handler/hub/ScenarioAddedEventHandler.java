package ru.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Service;
import ru.practicum.telemetry.collector.model.DeviceAction;
import ru.practicum.telemetry.collector.model.ScenarioCondition;
import ru.practicum.telemetry.collector.model.hub.HubEvent;
import ru.practicum.telemetry.collector.model.hub.HubEventType;
import ru.practicum.telemetry.collector.model.hub.ScenarioAddedEvent;
import ru.practicum.telemetry.collector.service.KafkaEventProducer;
import ru.practicum.telemetry.collector.utils.EnumMapper;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;

@Service
public class ScenarioAddedEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {
    public ScenarioAddedEventHandler(KafkaEventProducer kafkaEventProducer) {
        super(kafkaEventProducer);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_ADDED;
    }

    @Override
    public ScenarioAddedEventAvro mapToAvro(HubEvent event) {
        ScenarioAddedEvent scenarioAddedEvent = (ScenarioAddedEvent) event;
        return ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAddedEvent.getName())
                .setConditions(mapConditionsToAvro(scenarioAddedEvent.getConditions()))
                .setActions(mapActionsToAvro(scenarioAddedEvent.getActions()))
                .build();
    }

    private ScenarioConditionAvro mapScenarioConditionToAvro(ScenarioCondition condition) {
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(EnumMapper.map(condition.getType(), ConditionTypeAvro.class))
                .setOperation(EnumMapper.map(condition.getOperation(), ConditionOperationAvro.class))
                .setValue(condition.getValue())
                .build();
    }

    private List<ScenarioConditionAvro> mapConditionsToAvro(List<ScenarioCondition> conditions) {
        return conditions.stream()
                .map(this::mapScenarioConditionToAvro)
                .toList();
    }

    private DeviceActionAvro mapDeviceActionToAvro(DeviceAction action) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(EnumMapper.map(action.getType(), ActionTypeAvro.class))
                .setValue(action.getValue())
                .build();
    }

    private List<DeviceActionAvro> mapActionsToAvro(List<DeviceAction> actions) {
        return actions.stream()
                .map(this::mapDeviceActionToAvro)
                .toList();
    }
}
