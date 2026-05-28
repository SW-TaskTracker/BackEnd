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

@Component
@RequiredArgsConstructor
public class AchievementCalculator {

    private final HabitRepository habitRepository;
    private final CheckInRepository checkInRepository;

    /**
     * 이번 달 1일 ~ 오늘까지 달성률 계산
     */
    public double calcThisMonthRate(Long userId) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate start = today.withDayOfMonth(1);

        List<Habit> activeHabits = habitRepository.findByUserIdAndDeletedAtIsNull(userId);
        int targetCount = countTargetDaysInPeriod(activeHabits, start, today);
        int completedCount = checkInRepository.findCompletedHabitIdsByPeriod(userId, start, today).size();

        return calcRate(completedCount, targetCount);
    }

    public int countTargetDaysInPeriod(List<Habit> habits, LocalDate start, LocalDate end) {
        int total = 0;
        for (Habit habit : habits) {
            total += countDaysInPeriod(habit.getCustomDays(), start, end);
        }
        return total;
    }

    public int countDaysInPeriod(Set<DayOfWeek> targetDays, LocalDate start, LocalDate end) {
        int count = 0;
        LocalDate cursor = start;
        while (!cursor.isAfter(end)) {
            if (targetDays.contains(cursor.getDayOfWeek())) {
                count++;
            }
            cursor = cursor.plusDays(1);
        }
        return count;
    }

    public double calcRate(int completed, int target) {
        if (target == 0) return 0.0;
        return Math.round((double) completed / target * 100 * 10) / 10.0;
    }
}
