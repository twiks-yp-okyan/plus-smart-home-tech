package ru.practicum.telemetry.collector.model.sensor;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class LightSensorEvent extends SensorEvent {
    private final int linkQuality;
    private final int luminosity;

    public LightSensorEvent(
            String id,
            String hubId,
            int linkQuality,
            int luminosity
    ) {
        super(id, hubId);

        this.linkQuality = linkQuality;
        this.luminosity = luminosity;
    }

    @Override
    public SensorEventType getType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}
