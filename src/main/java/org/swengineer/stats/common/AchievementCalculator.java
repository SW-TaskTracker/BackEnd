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

    /**
     * 이번 달 달성률 계산
     * 삭제된 습관의 체크인은 분자에서 제외
     */
    public double calcThisMonthRate(Long userId) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate start = today.withDayOfMonth(1);

        List<Habit> activeHabits = habitRepository.findByUserIdAndDeletedAtIsNull(userId);
        Set<Long> activeHabitIds = activeHabits.stream()
                .map(Habit::getId)
                .collect(Collectors.toSet());

        int targetCount = countTargetDaysInPeriod(activeHabits, start, today);
        int completedCount = (int) checkInRepository
                .findCompletedHabitIdsByPeriod(userId, start, today)
                .stream()
                .filter(activeHabitIds::contains)
                .count();

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
