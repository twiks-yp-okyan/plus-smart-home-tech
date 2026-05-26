package ru.practicum.telemetry.collector.utils;

import org.mapstruct.Mapper;
import ru.practicum.telemetry.collector.model.sensor.SensorEventType;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

@Mapper(componentModel = "spring")
public interface TypeMapper {
    DeviceTypeAvro sensorToDeviceToAvro(SensorEventType sensorType);
}
