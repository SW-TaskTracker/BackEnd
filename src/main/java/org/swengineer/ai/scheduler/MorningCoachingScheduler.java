package org.swengineer.ai.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.swengineer.ai.dto.context.MorningCoachingContext;
import org.swengineer.ai.service.AiCoachingService;
import org.swengineer.habit.service.HabitService;
import org.swengineer.stats.common.AchievementCalculator;
import org.swengineer.user.entity.User;
import org.swengineer.user.service.UserService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MorningCoachingScheduler {

    private final UserService userService;
    private final AiCoachingService aiCoachingService;
    private final HabitService habitService;

    private final AchievementCalculator achievementCalculator;

    @Scheduled(cron = " 0 0 7 * * *", zone = "Asia/Seoul")
    public void generateMorningCoachingMessages() {
        log.info("아침 코칭 메시지 배치 시작");


        List<User> users = userService.getActiveUsers();


        for (User user : users) {
            try {
                Long userId = user.getId();

// 생성자 방식
                MorningCoachingContext context = new MorningCoachingContext(
                        habitService.getMaxStreak(userId),
                        habitService.getTodayHabitNames(userId),
                        achievementCalculator.calcThisMonthRate(userId)
                );
                String message = aiCoachingService.generatingMorningMessage(context);
                // TODO: 피드에 저장하는 로직

            } catch (Exception e) {
                log.error("유저 {} 코칭 메세지 생성 실패 :{}", user.getId(), e.getMessage());
            }
        }
    }
}
