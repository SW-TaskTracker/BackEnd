package org.swengineer.habit.dto.response;

import org.swengineer.habit.entity.Habit;
import org.swengineer.habit.entity.enums.FrequencyType;
import org.swengineer.habit.entity.enums.HabitCategory;

import java.time.DayOfWeek;
import java.util.Set;

public record HabitListResponse(
        Long id,
        String name,
        HabitCategory category,
        FrequencyType frequencyType,
        Set<DayOfWeek> customDays,
        boolean completedToday,
        int streak
) {
    public static HabitListResponse of(Habit habit, boolean completedToday, int streak) {
        return new HabitListResponse(
                habit.getId(),
                habit.getName(),
                habit.getCategory(),
                habit.getFrequencyType(),
                habit.getCustomDays(),
                completedToday,
                streak
        );
    }
}