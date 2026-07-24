package ru.practicum.telemetry.analyzer.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsumerProperties {
    private String topic;
    private String groupId;
    private Integer maxPollRecords;
    private DeserializerType deserializer;

    public enum DeserializerType {
        HUB,
        SNAPSHOT
    }
}