package ru.practicum.telemetry.collector.model.sensor;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class MotionSensorEvent extends SensorEvent {
    private final int linkQuality;
    private final boolean motion;
    private final int voltage;

    public MotionSensorEvent(
            String id,
            String hubId,
            int linkQuality,
            boolean motion,
            int voltage
    ) {
        super(id, hubId);

        this.linkQuality = linkQuality;
        this.motion = motion;
        this.voltage = voltage;
    }

    @Override
    public SensorEventType getType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}
