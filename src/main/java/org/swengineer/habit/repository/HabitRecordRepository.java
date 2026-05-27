package org.swengineer.habit.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.swengineer.habit.entity.HabitRecord;

import java.time.LocalDate;
import java.util.List;

public interface HabitRecordRepository extends JpaRepository<HabitRecord, Long> {
    boolean existsByHabitIdAndRecordDate(Long habitId, LocalDate date);
    List<HabitRecord> findByHabitIdIn(List<Long> habitIds);
    List<HabitRecord> findByHabitIdAndRecordDateBetween(Long habitId, LocalDate start, LocalDate end);
}
