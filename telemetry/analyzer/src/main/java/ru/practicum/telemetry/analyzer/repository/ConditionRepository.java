package ru.practicum.telemetry.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.telemetry.analyzer.model.Condition;

public interface ConditionRepository extends JpaRepository<Condition, Long> {
}