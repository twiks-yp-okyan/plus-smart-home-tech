package ru.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.telemetry.analyzer.exception.NotFoundException;
import ru.practicum.telemetry.analyzer.model.Action;
import ru.practicum.telemetry.analyzer.model.Scenario;
import ru.practicum.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScenarioService {
    private final ScenarioRepository repository;
    private final ConditionService conditionService;

    public Map<String, Action> scenarioActions(String hubId, String scenarioName) {
        Scenario scenario = repository.findByHubIdAndName(hubId, scenarioName).orElseThrow(
                () -> new NotFoundException(
                        String.format("Сценарий для хаба %s по имени %s не найден.", hubId, scenarioName)
                )
        );
        return scenario.getActions();
    }

    public List<String> checkHubScenarios(String hubId, SensorsSnapshotAvro snapshot) {
        List<String> successScenarios = new ArrayList<>();
        List<Scenario> scenarios = getHubScenarios(hubId);
        return scenarios.stream()
                .peek(scenario -> log.debug("Проверка сценария - {} для хаба - {}", scenario.getName(), scenario.getHubId()))
                .filter(scenario -> scenarioConditionsSuccess(scenario, snapshot))
                .map(Scenario::getName)
                .toList();
    }

    private boolean scenarioConditionsSuccess(Scenario scenario, SensorsSnapshotAvro snapshot) {
        // Проверить все условия из сценария. Если ВСЕ условия выполнены, то сценарий выполнен.
        // Тогда отправляем список Action для сценария
        return scenario.getConditions().entrySet().stream()
                .peek(e -> log.debug("Проверка условия сценария для датчика - {}", e.getKey()))
                .allMatch(
                        e -> conditionService.conditionSuccess(
                                e.getValue(), snapshot.getSensorsState().get(e.getKey())
                        )
                );
    }

    private List<Scenario> getHubScenarios(String hubId) {
        return repository.findByHubId(hubId);
    }
}
