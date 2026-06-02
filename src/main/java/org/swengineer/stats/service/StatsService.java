package org.swengineer.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swengineer.checkin.repository.CheckInRepository;
import org.swengineer.global.api.exception.CustomException;
import org.swengineer.habit.entity.Habit;
import org.swengineer.habit.entity.enums.HabitCategory;
import org.swengineer.habit.repository.HabitRepository;
import org.swengineer.stats.common.AchievementCalculator;
import org.swengineer.stats.dto.response.CategoryAchievementResponse;
import org.swengineer.stats.dto.response.DailyAchievementResponse;
import org.swengineer.stats.dto.response.StatsDashboardResponse;
import org.swengineer.user.code.UserErrorCode;
import org.swengineer.user.entity.User;
import org.swengineer.user.repository.UserRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final UserRepository userRepository;
    private final HabitRepository habitRepository;
    private final CheckInRepository checkInRepository;
    private final AchievementCalculator achievementCalculator;

    public StatsDashboardResponse getDashboard(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        LocalDate thisMonthStart = today.withDayOfMonth(1);
        LocalDate thisMonthEnd = today;
        LocalDate lastMonthStart = thisMonthStart.minusMonths(1);
        LocalDate lastMonthEnd = lastMonthStart.withDayOfMonth(lastMonthStart.lengthOfMonth());
        LocalDate weekMonday = today.with(WeekFields.ISO.dayOfWeek(), 1);
        LocalDate weekSunday = weekMonday.plusDays(6);

        List<Habit> activeHabits = habitRepository.findByUserIdAndDeletedAtIsNull(userId);

        // 핵심 수정: 활성 습관 ID Set으로 삭제된 습관의 체크인을 분자에서 제외
        Set<Long> activeHabitIds = activeHabits.stream()
                .map(Habit::getId)
                .collect(Collectors.toSet());

        List<Long> thisMonthCompletedList =
                checkInRepository.findCompletedHabitIdsByPeriod(userId, thisMonthStart, thisMonthEnd)
                        .stream()
                        .filter(activeHabitIds::contains)
                        .toList();

        List<Habit> lastMonthHabits = habitRepository.findHabitsActiveInPeriod(
                userId, lastMonthStart.atStartOfDay(), lastMonthEnd.atTime(23, 59, 59));
        Set<Long> lastMonthHabitIds = lastMonthHabits.stream()
                .map(Habit::getId)
                .collect(Collectors.toSet());
        List<Long> lastMonthCompletedList =
                checkInRepository.findCompletedHabitIdsByPeriod(userId, lastMonthStart, lastMonthEnd)
                        .stream()
                        .filter(lastMonthHabitIds::contains)
                        .toList();

        int thisMonthTarget = achievementCalculator.countTargetDaysInPeriod(activeHabits, thisMonthStart, thisMonthEnd);
        double thisMonthRate = achievementCalculator.calcRate(thisMonthCompletedList.size(), thisMonthTarget);

        int lastMonthTarget = achievementCalculator.countTargetDaysInPeriod(lastMonthHabits, lastMonthStart, lastMonthEnd);
        double lastMonthRate = achievementCalculator.calcRate(lastMonthCompletedList.size(), lastMonthTarget);

        double improved = Math.round((thisMonthRate - lastMonthRate) * 10) / 10.0;

        List<DailyAchievementResponse> dailyAchievements =
                calcWeeklyDailyAchievements(activeHabits, activeHabitIds, userId, weekMonday, weekSunday, today);

        List<CategoryAchievementResponse> categoryAchievements =
                calcCategoryAchievements(activeHabits, thisMonthCompletedList, thisMonthStart, thisMonthEnd);

        return new StatsDashboardResponse(user.getNickname(), thisMonthRate, improved, dailyAchievements, categoryAchievements);
    }

    private List<DailyAchievementResponse> calcWeeklyDailyAchievements(
            List<Habit> habits, Set<Long> activeHabitIds, Long userId,
            LocalDate weekStart, LocalDate weekEnd, LocalDate today) {

        List<DailyAchievementResponse> result = new ArrayList<>();
        LocalDate cursor = weekStart;

        while (!cursor.isAfter(weekEnd)) {
            final LocalDate date = cursor;
            DayOfWeek dow = date.getDayOfWeek();

            int targetCount = (int) habits.stream()
                    .filter(h -> h.getCustomDays().contains(dow))
                    .count();

            int completedCount = 0;
            double rate = 0.0;

            if (!date.isAfter(today)) {
                Set<Long> completedIds = checkInRepository
                        .findCompletedHabitIdsByDate(userId, date)
                        .stream()
                        .filter(activeHabitIds::contains)
                        .collect(Collectors.toSet());

                completedCount = (int) habits.stream()
                        .filter(h -> h.getCustomDays().contains(dow))
                        .filter(h -> completedIds.contains(h.getId()))
                        .count();
                rate = achievementCalculator.calcRate(completedCount, targetCount);
            }

            result.add(new DailyAchievementResponse(date, completedCount, targetCount, rate));
            cursor = cursor.plusDays(1);
        }

        return result;
    }

    private List<CategoryAchievementResponse> calcCategoryAchievements(
            List<Habit> habits, List<Long> completedHabitIdList,
            LocalDate start, LocalDate end) {

        Map<Long, Long> completedCountByHabitId = completedHabitIdList.stream()
                .collect(Collectors.groupingBy(id -> id, Collectors.counting()));

        List<CategoryAchievementResponse> result = new ArrayList<>();

        for (HabitCategory category : HabitCategory.values()) {
            List<Habit> categoryHabits = habits.stream()
                    .filter(h -> h.getCategory() == category)
                    .toList();

            if (categoryHabits.isEmpty()) continue;

            int targetCount = achievementCalculator.countTargetDaysInPeriod(categoryHabits, start, end);
            int completedCount = categoryHabits.stream()
                    .mapToInt(h -> completedCountByHabitId.getOrDefault(h.getId(), 0L).intValue())
                    .sum();

            result.add(new CategoryAchievementResponse(
                    category, completedCount, targetCount,
                    achievementCalculator.calcRate(completedCount, targetCount)
            ));
        }

        return result;
    }
}
