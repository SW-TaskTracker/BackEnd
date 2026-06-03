package org.swengineer.stats.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.swengineer.checkin.repository.CheckInRepository;
import org.swengineer.habit.entity.Habit;
import org.swengineer.habit.repository.HabitRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AchievementCalculator {

    private final HabitRepository habitRepository;
    private final CheckInRepository checkInRepository;

    public double calcThisMonthRate(Long userId) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate start = today.withDayOfMonth(1);

        List<Habit> periodHabits = habitRepository.findHabitsActiveInPeriod(
                userId, start.atStartOfDay(), today.atTime(23, 59, 59));
        Set<Long> periodHabitIds = periodHabits.stream()
                .map(Habit::getId).collect(Collectors.toSet());

        int targetCount = countTargetDaysInPeriodForHabits(periodHabits, start, today);
        int completedCount = (int) checkInRepository
                .findCompletedHabitIdsByPeriod(userId, start, today)
                .stream().filter(periodHabitIds::contains).count();

        return calcRate(completedCount, targetCount);
    }

    public int countTargetDaysInPeriodForHabits(List<Habit> habits, LocalDate periodStart, LocalDate periodEnd) {
        int total = 0;
        for (Habit habit : habits) {
            total += countEffectiveDays(habit, periodStart, periodEnd);
        }
        return total;
    }

    private int countEffectiveDays(Habit habit, LocalDate periodStart, LocalDate periodEnd) {
        LocalDate habitStart = habit.getCreatedAt().toLocalDate();
        LocalDate effectiveStart = habitStart.isAfter(periodStart) ? habitStart : periodStart;

        LocalDate effectiveEnd;
        if (habit.getDeletedAt() != null) {
            // 삭제 당일도 분모에 포함 (minusDays 제거)
            LocalDate deletedDate = habit.getDeletedAt().toLocalDate();
            effectiveEnd = deletedDate.isBefore(periodEnd) ? deletedDate : periodEnd;
        } else {
            effectiveEnd = periodEnd;
        }

        if (effectiveStart.isAfter(effectiveEnd)) return 0;

        return countDaysInPeriod(habit.getCustomDays(), effectiveStart, effectiveEnd);
    }

    public int countDaysInPeriod(Set<DayOfWeek> targetDays, LocalDate start, LocalDate end) {
        int count = 0;
        LocalDate cursor = start;
        while (!cursor.isAfter(end)) {
            if (targetDays.contains(cursor.getDayOfWeek())) count++;
            cursor = cursor.plusDays(1);
        }
        return count;
    }

    public double calcRate(int completed, int target) {
        if (target == 0) return 0.0;
        return Math.round((double) completed / target * 100 * 10) / 10.0;
    }
}
