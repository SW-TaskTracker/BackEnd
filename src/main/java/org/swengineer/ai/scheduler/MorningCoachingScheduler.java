package org.swengineer.ai.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.swengineer.ai.service.AiCoachingService;
import org.swengineer.user.entity.User;
import org.swengineer.user.service.UserService;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MorningCoachingScheduler {

    private final UserService userService;
    private final AiCoachingService aiCoachingService;

    @Scheduled(cron = "0 0 7 * * *", zone = "Asia/Seoul")
    public void generateMorningCoachingMessages() {
        log.info("아침 코칭 메시지 배치 시작");

        List<User> users = userService.getActiveUsers();
        for (User user : users) {
            aiCoachingService.generateAndSaveForUser(user.getId());
        }

        log.info("아침 코칭 메시지 배치 완료");
    }
}
