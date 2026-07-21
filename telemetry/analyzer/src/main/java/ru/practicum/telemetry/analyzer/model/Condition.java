package ru.practicum.telemetry.analyzer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;

@Entity
@Table(name = "conditions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Condition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ConditionTypeAvro type;
    @Column(name = "operation")
    @Enumerated(EnumType.STRING)
    private ConditionOperationAvro operation;
    @Column(name = "value")
    private Integer value;
}
