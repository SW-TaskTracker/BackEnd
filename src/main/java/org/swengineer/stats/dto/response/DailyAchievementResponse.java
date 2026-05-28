package org.swengineer.stats.dto.response;

import java.time.LocalDate;

public record DailyAchievementResponse(
        LocalDate date,
        int completedCount,  // 실제 완료한 습관 수
        int targetCount,     // 그날 해야 할 습관 수
        double rate          // completedCount / targetCount * 100
) {}
