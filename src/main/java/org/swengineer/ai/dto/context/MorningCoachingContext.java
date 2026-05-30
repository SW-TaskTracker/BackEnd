package org.swengineer.ai.dto.context;

import java.util.List;

public record MorningCoachingContext(
        int currentStreak,
        List<String> todayHabits,
        double achievementRate
) {}