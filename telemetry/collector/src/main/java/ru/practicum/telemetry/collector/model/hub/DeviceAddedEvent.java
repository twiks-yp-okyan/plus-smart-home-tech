package ru.practicum.telemetry.collector.model.hub;

import lombok.*;
import ru.practicum.telemetry.collector.model.sensor.SensorEventType;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceAddedEvent extends HubEvent {
    private final String id;
    private final SensorEventType deviceType;

    public DeviceAddedEvent(String hubId, String id, SensorEventType deviceType) {
        super(hubId);

        this.id = id;
        this.deviceType = deviceType;
    }

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }
}
