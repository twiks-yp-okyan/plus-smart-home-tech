package ru.practicum.telemetry.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.telemetry.analyzer.model.Condition;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Service
@Slf4j
public class ConditionService {
    public boolean conditionSuccess(Condition condition, SensorStateAvro sensorState) {
        if (sensorState == null) {
            log.debug("Для датчика нет данных в снэпшоте");
            return false;
        }
        Object sensorStateValue = extractValue(sensorState.getData(), condition.getType());
        return compareValue(sensorStateValue, condition.getOperation(), condition.getValue());
    }

    private Object extractValue(Object sensorStateData, ConditionTypeAvro conditionType) {
        switch (conditionType) {
            case MOTION:
                if (sensorStateData instanceof MotionSensorAvro) {
                    return ((MotionSensorAvro) sensorStateData).getMotion();
                }
                break;
            case LUMINOSITY:
                if (sensorStateData instanceof LightSensorAvro) {
                    return ((LightSensorAvro) sensorStateData).getLuminosity();
                }
                break;
            case SWITCH:
                if (sensorStateData instanceof SwitchSensorAvro) {
                    return ((SwitchSensorAvro) sensorStateData).getState();
                }
                break;
            case TEMPERATURE:
                if (sensorStateData instanceof ClimateSensorAvro) {
                    return ((ClimateSensorAvro) sensorStateData).getTemperatureC();
                } else if (sensorStateData instanceof TemperatureSensorAvro) {
                    return ((TemperatureSensorAvro) sensorStateData).getTemperatureC();
                }
                break;
            case CO2LEVEL:
                if (sensorStateData instanceof ClimateSensorAvro) {
                    return ((ClimateSensorAvro) sensorStateData).getCo2Level();
                }
                break;
            case HUMIDITY:
                if (sensorStateData instanceof ClimateSensorAvro) {
                    return ((ClimateSensorAvro) sensorStateData).getHumidity();
                }
                break;
            default:
                return null;
        }
        return null;
    }

    private boolean compareValue(Object value, ConditionOperationAvro operation, Integer threshold) {
        log.debug("Сравнение: value={}, operation={}, threshold={}", value, operation, threshold);

        return switch (value) {
            case Boolean boolValue -> compareBoolean(boolValue, operation, threshold);
            case Integer intValue -> compareInteger(intValue, operation, threshold);
            default -> false;
        };
    }

    private boolean compareBoolean(Boolean value, ConditionOperationAvro operation, Integer threshold) {
        Integer intValue = value ? 1 : 0;
        return operation.equals(ConditionOperationAvro.EQUALS) && intValue.compareTo(threshold) == 0;
    }

    private boolean compareInteger(Integer value, ConditionOperationAvro operation, Integer threshold) {
        return switch (operation) {
            case EQUALS -> value.compareTo(threshold) == 0;
            case LOWER_THAN -> value.compareTo(threshold) < 0;
            case GREATER_THAN -> value.compareTo(threshold) > 0;
        };
    }
}
