package ru.practicum.telemetry.collector.service.handler;

import ru.practicum.telemetry.collector.model.sensor.SensorEvent;

public interface SensorEventHandler<T> {
    SensorEvent getMessageType();

    T handle(SensorEvent event);
}
