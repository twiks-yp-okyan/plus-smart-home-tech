package ru.practicum.telemetry.collector.model.sensor;

public class SwitchSensorEvent extends SensorEvent {
    private boolean state;

    @Override
    public SensorEventType getType() {
        return SensorEventType.SWITCH_SENSOR;
    }
}
