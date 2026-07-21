package ru.practicum.telemetry.analyzer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;

@Entity
@Table(name = "actions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ActionTypeAvro type;
    @Column(name = "value")
    private Integer value;
}
