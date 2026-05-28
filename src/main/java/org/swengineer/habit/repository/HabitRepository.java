package org.swengineer.habit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.swengineer.habit.entity.Habit;
import org.swengineer.habit.entity.enums.HabitCategory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    /**
     * 특정 기간에 활성이었던 습관 조회 (지난달 달성률 계산용)
     * - 기간 시작일 이전에 생성되었고
     * - 삭제되지 않았거나, 기간 종료일 이후에 삭제된 습관
     */
    @Query("""
        SELECT h FROM Habit h
        WHERE h.userId = :userId
          AND h.createdAt <= :periodEnd
          AND (h.deletedAt IS NULL OR h.deletedAt >= :periodStart)
        """)
    List<Habit> findHabitsActiveInPeriod(
            @Param("userId") Long userId,
            @Param("periodStart") LocalDateTime periodStart,
            @Param("periodEnd") LocalDateTime periodEnd
    );
}
