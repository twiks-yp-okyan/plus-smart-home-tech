package ru.practicum.telemetry.collector.model;

import lombok.Data;

@Data
public class ScenarioCondition {
    private String sensorId;
    private ScenarioConditionType type;
    private ConditionsOperations operation;
    private Integer value;
}
