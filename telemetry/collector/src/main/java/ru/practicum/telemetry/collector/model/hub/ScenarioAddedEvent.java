package ru.practicum.telemetry.collector.model.hub;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.ToString;
import ru.practicum.telemetry.collector.model.DeviceAction;
import ru.practicum.telemetry.collector.model.ScenarioCondition;

import java.util.List;

@Getter
@ToString(callSuper = true)
public class ScenarioAddedEvent extends HubEvent {
    @Size(min = 3)
    private final String name;
    @NotEmpty
    private final List<ScenarioCondition> conditions;
    @NotEmpty
    private final List<DeviceAction> actions;

    public ScenarioAddedEvent(
            String hubId,
            String name,
            List<ScenarioCondition> conditions,
            List<DeviceAction> actions
    ) {
        super(hubId);

        this.name = name;
        this.conditions = conditions;
        this.actions = actions;
    }

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
