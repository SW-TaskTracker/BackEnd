package org.swengineer.stats.dto.response;

import org.swengineer.habit.entity.enums.HabitCategory;

public record CategoryAchievementResponse(
        HabitCategory category,
        int completedCount,
        int targetCount,
        double rate
) {}
