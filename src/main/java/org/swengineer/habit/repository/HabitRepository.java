package org.swengineer.habit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.swengineer.habit.entity.Habit;
import org.swengineer.habit.entity.enums.HabitCategory;

import java.time.DayOfWeek;
import java.util.List;

public interface HabitRepository extends JpaRepository<Habit, Long> {

    // deletedAt이 null인 것 = 활성 습관
    int countByUserIdAndDeletedAtIsNull(Long userId);
    List<Habit> findByUserIdAndDeletedAtIsNull(Long userId);
    @Query("SELECT h FROM Habit h JOIN h.customDays d " +
            "WHERE h.userId = :userId AND h.deletedAt IS NULL AND d = :dayOfWeek")
    List<Habit> findByUserIdAndDayOfWeek(@Param("userId") Long userId,
                                         @Param("dayOfWeek") DayOfWeek dayOfWeek);

    @Query("SELECT h FROM Habit h JOIN h.customDays d " +
            "WHERE h.userId = :userId AND h.deletedAt IS NULL " +
            "AND d = :dayOfWeek AND h.category = :category")
    List<Habit> findByUserIdAndDayOfWeekAndCategory(@Param("userId") Long userId,
                                                    @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                                    @Param("category") HabitCategory category);
}
