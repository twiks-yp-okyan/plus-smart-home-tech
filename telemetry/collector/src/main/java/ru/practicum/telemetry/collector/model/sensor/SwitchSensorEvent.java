package ru.practicum.telemetry.collector.model.sensor;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class SwitchSensorEvent extends SensorEvent {
    private final boolean state;

    public SwitchSensorEvent(String id, String hubId, boolean state) {
        super(id, hubId);

        this.state = state;
    }

    @Override
    public SensorEventType getType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }
}
