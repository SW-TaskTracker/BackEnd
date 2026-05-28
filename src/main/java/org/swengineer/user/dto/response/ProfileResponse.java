package org.swengineer.user.dto.response;

public record ProfileResponse(
        String nickname,
        int activeHabitCount,
        double monthlyAchievementRate
) {}
