package ru.practicum.telemetry.analyzer.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.telemetry.analyzer.model.Action;
import ru.practicum.telemetry.analyzer.model.Condition;
import ru.practicum.telemetry.analyzer.model.Scenario;
import ru.practicum.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScenarioAddedEventHandler implements HubEventHandler {
    private final ScenarioRepository repository;

    @Override
    public String getPayloadType() {
        return ScenarioAddedEventAvro.class.getSimpleName();
    }

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        ScenarioAddedEventAvro scenarioAdded = (ScenarioAddedEventAvro) event.getPayload();
        log.debug("Save new scenario with name - {} for hub - {}", scenarioAdded.getName(), event.getHubId());
        Scenario scenario = repository.findByHubIdAndName(event.getHubId(), scenarioAdded.getName())
                .orElse(null);
        if (scenario == null) {
            repository.save(mapToScenario(event.getHubId(), scenarioAdded));
        }
    }

    private Scenario mapToScenario(String hubId, ScenarioAddedEventAvro scenarioAdded) {
        Map<String, Condition> conditions = mapConditions(scenarioAdded.getConditions());
        Map<String, Action> actions = mapToActions(scenarioAdded.getActions());

        return Scenario.builder()
                .hubId(hubId)
                .name(scenarioAdded.getName())
                .conditions(conditions)
                .actions(actions)
                .build();
    }

    private Map<String, Condition> mapConditions(List<ScenarioConditionAvro> conditionsAvro) {
        return conditionsAvro.stream()
                .collect(Collectors.toMap(ScenarioConditionAvro::getSensorId, this::mapToCondition));
    }

    private Condition mapToCondition(ScenarioConditionAvro conditionAvro) {
        Integer conditionValue = switch (conditionAvro.getValue()) {
            case Boolean boolValue -> boolValue ? 1 : 0;
            case Integer intValue -> intValue;
            case null, default -> 0;
        };

        return Condition.builder()
                .type(conditionAvro.getType())
                .operation(conditionAvro.getOperation())
                .value(conditionValue)
                .build();
    }

    private Map<String, Action> mapToActions(List<DeviceActionAvro> actionsAvro) {
        return actionsAvro.stream()
                .collect(Collectors.toMap(DeviceActionAvro::getSensorId, this::mapToAction));
    }

    private Action mapToAction(DeviceActionAvro actionAvro) {
        return Action.builder()
                .type(actionAvro.getType())
                .value(actionAvro.getValue())
                .build();
    }
}
