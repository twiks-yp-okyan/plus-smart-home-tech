package ru.practicum.telemetry.collector.model.hub;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class ScenarioRemovedEvent extends HubEvent {
    @Size(min = 3)
    private final String name;

    public ScenarioRemovedEvent (String hubId, String name) {
        super(hubId);

        this.name = name;
    }

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED;
    }
}
