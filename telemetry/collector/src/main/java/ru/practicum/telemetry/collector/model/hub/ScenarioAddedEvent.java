package ru.practicum.telemetry.collector.model.hub;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import ru.practicum.telemetry.collector.model.DeviceAction;
import ru.practicum.telemetry.collector.model.ScenarioCondition;

import java.util.List;

public class ScenarioAddedEvent extends HubEvent {
    @Size(min = 3)
    private String name;
    @NotEmpty
    private List<ScenarioCondition> conditions;
    @NotEmpty
    private List<DeviceAction> actions;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
