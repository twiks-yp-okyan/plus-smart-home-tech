package ru.practicum.telemetry.collector.model.hub;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class DeviceRemovedEvent extends HubEvent {
    private final String id;

    public DeviceRemovedEvent(String hubId, String id) {
        super(hubId);

        this.id = id;
    }

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }
}
