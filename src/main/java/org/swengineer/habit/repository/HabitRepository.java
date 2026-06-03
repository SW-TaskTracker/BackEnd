package org.swengineer.habit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.swengineer.habit.entity.Habit;
import org.swengineer.habit.entity.enums.HabitCategory;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

public interface HabitRepository extends JpaRepository<Habit, Long> {

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
     * 특정 기간에 활성이었던 습관 조회 (삭제된 습관 포함)
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

    /**
     * 히스토리/일별 달성률용: 특정 날짜 당시 활성이었고 해당 요일에 해당하는 습관
     *
     * - createdAt <= dayEnd (당일 23:59:59): 당일 중에 생성된 것도 포함
     * - deletedAt IS NULL OR deletedAt >= dayStart (당일 00:00:00): 당일 중에 삭제된 것도 포함
     */
    @Query("""
        SELECT h FROM Habit h JOIN h.customDays d
        WHERE h.userId = :userId
          AND d = :dayOfWeek
          AND h.createdAt <= :dayEnd
          AND (h.deletedAt IS NULL OR h.deletedAt >= :dayStart)
        """)
    List<Habit> findHabitsActiveOnDate(
            @Param("userId") Long userId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd
    );
}
