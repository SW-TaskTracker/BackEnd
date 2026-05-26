package org.swengineer.habit.dto.response;

import org.swengineer.habit.entity.Habit;
import org.swengineer.habit.entity.enums.FrequencyType;
import org.swengineer.habit.entity.enums.HabitCategory;

import java.time.DayOfWeek;
import java.util.Set;

public record HabitResponse(
        Long id,
        String name,
        HabitCategory category,
        FrequencyType frequencyType,
        Set<DayOfWeek> customDays
) {
    public static HabitResponse from(Habit habit) {
        return new HabitResponse(
                habit.getId(),
                habit.getName(),
                habit.getCategory(),
                habit.getFrequencyType(),
                habit.getCustomDays()
        );
    }
}