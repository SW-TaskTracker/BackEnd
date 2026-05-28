package org.swengineer.habit.dto.response;

import java.util.List;

public record TodayHabitResponse(
        int totalCount,
        int completedCount,
        int completionRate,
        int maxStreak,
        List<HabitListResponse> habits
) {
    public static TodayHabitResponse of(List<HabitListResponse> habits) {
        int total = habits.size();
        int completed = (int) habits.stream().filter(HabitListResponse::completedToday).count();
        int rate = total > 0 ? (completed * 100) / total : 0;
        int maxStreak = habits.stream()
                .mapToInt(HabitListResponse::streak)
                .max()
                .orElse(0);

        return new TodayHabitResponse(total, completed, rate, maxStreak, habits);
    }
}