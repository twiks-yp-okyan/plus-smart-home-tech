package ru.practicum.telemetry.collector.model.sensor;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class TemperatureSensorEvent extends SensorEvent {
    private final int temperatureC;
    private final int temperatureF;

    public TemperatureSensorEvent(
            String id,
            String hubId,
            int temperatureC,
            int temperatureF
    ) {
        super(id, hubId);

        this.temperatureC = temperatureC;
        this.temperatureF = temperatureF;
    }

    @Override
    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}
