package org.swengineer.ai.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.swengineer.ai.entity.AiInsight;
import org.swengineer.ai.repository.AiInsightRepository;
import org.swengineer.ai.service.AiInsightService;
import org.swengineer.checkin.repository.CheckInRepository;
import org.swengineer.user.entity.User;
import org.swengineer.user.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeeklyInsightScheduler {


    private final UserRepository userRepository;
    private final AiInsightService aiInsightService;
    private final CheckInRepository checkInRepository;
    private final AiInsightRepository aiInsightRepository;


    @Scheduled(cron = "0 50 23 * * SUN", zone = "Asia/Seoul")
    public void generateWeeklyInsights() {
        log.info("주간 AI 인사이트 배치 시작");

        List<User> users = userRepository.findAllByDeletedAtIsNull();

        for (User user : users) {
            try {
                Long userId = user.getId();

                // 7일 이상 체크인 데이터 있는지 확인
                LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
                int checkInCount = checkInRepository
                        .countByUserIdAndCheckedDateAfter(userId, sevenDaysAgo);

                if (checkInCount < 1) {
                    log.info("유저 {} 데이터 부족으로 인사이트 생성 스킵", userId);
                    continue;
                }

                String insight = aiInsightService.generateInsight(userId);
                aiInsightRepository.save(AiInsight.create(userId, insight));

            } catch (Exception e) {
                log.error("유저 {} 인사이트 생성 실패: {}", user.getId(), e.getMessage());
            }
        }
    }

}
