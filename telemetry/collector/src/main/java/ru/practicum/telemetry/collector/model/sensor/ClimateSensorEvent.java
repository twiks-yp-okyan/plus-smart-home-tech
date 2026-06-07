package ru.practicum.telemetry.collector.model.sensor;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class ClimateSensorEvent extends SensorEvent {
    private final int temperatureC;
    private final int humidity;
    private final int co2Level;

    public ClimateSensorEvent(
            String id,
            String hubId,
            int temperatureC,
            int humidity,
            int co2Level
    ) {
        super(id, hubId);

        this.temperatureC = temperatureC;
        this.humidity = humidity;
        this.co2Level = co2Level;
    }

    @Override
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}
