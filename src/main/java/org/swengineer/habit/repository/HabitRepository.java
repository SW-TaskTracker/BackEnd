package org.swengineer.habit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.swengineer.habit.entity.Habit;

import java.util.List;

public interface HabitRepository extends JpaRepository<Habit, Long> {

    // deletedAt이 null인 것 = 활성 습관
    int countByUserIdAndDeletedAtIsNull(Long userId);
    List<Habit> findByUserIdAndDeletedAtIsNull(Long userId);
}
