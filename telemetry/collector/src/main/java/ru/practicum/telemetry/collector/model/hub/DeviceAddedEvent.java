package ru.practicum.telemetry.collector.model.hub;

import lombok.*;
import ru.practicum.telemetry.collector.model.sensor.SensorEventType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeviceAddedEvent extends HubEvent {
    private String id;
    private SensorEventType deviceType;

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }

//    public String getId() {
//        return id;
//    }
//
//    public SensorEventType getDeviceType() {
//        return deviceType;
//    }
}
