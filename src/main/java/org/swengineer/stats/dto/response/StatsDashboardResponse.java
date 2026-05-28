package org.swengineer.stats.dto.response;

import java.util.List;

public record StatsDashboardResponse(
        String nickname,

        // 이번 달 전체 달성률
        double monthlyAchievementRate,

        // 지난달 대비 향상률 (양수=향상, 음수=하락)
        // ex) 이번달 85%, 지난달 73% → +12.0
        double improvedFromLastMonth,

        // 이번 달 일별 완료율 (1일 ~ 오늘까지)
        List<DailyAchievementResponse> dailyAchievements,

        // 이번 달 카테고리별 달성률
        List<CategoryAchievementResponse> categoryAchievements
) {}
