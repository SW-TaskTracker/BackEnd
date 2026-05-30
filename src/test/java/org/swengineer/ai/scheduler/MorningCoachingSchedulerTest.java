package org.swengineer.ai.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.swengineer.ai.scheduler.MorningCoachingScheduler;

@SpringBootTest
@ActiveProfiles("test")
class MorningCoachingSchedulerTest {

    @Autowired
    MorningCoachingScheduler morningCoachingScheduler;

    @Test
    void 아침코칭_배치_테스트() {
        // 그냥 직접 호출
        morningCoachingScheduler.generateMorningCoachingMessages();
    }
}