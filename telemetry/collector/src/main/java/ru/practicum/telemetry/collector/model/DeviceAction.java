package ru.practicum.telemetry.collector.model;

import lombok.Data;

@Data
public class DeviceAction {
    private String sensorId;
    private DeviceActionType type;
    private Integer value;
}
