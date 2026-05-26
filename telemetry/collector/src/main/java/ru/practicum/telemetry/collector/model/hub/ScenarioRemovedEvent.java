package ru.practicum.telemetry.collector.model.hub;

import jakarta.validation.constraints.Size;

public class ScenarioRemovedEvent extends HubEvent {
    @Size(min = 3)
    private String name;

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}
