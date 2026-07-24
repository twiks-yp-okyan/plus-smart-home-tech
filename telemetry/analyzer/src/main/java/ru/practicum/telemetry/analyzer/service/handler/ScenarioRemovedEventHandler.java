package ru.practicum.telemetry.analyzer.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.telemetry.analyzer.exception.NotFoundException;
import ru.practicum.telemetry.analyzer.model.Scenario;
import ru.practicum.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScenarioRemovedEventHandler implements HubEventHandler {
    private final ScenarioRepository repository;

    @Override
    public String getPayloadType() {
        return ScenarioRemovedEventAvro.class.getSimpleName();
    }

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        ScenarioRemovedEventAvro scenarioRemoved = (ScenarioRemovedEventAvro) event.getPayload();
        Scenario existingScenario = repository.findByHubIdAndName(event.getHubId(), scenarioRemoved.getName())
                .orElseThrow(
                        () -> new NotFoundException(
                                String.format("Сценарий с названием = %s не найден для хаба с id = %s",
                                        scenarioRemoved.getName(), event.getHubId())
                        )
                );
        repository.delete(existingScenario);
    }
}
