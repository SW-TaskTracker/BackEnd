package org.swengineer.ai.scheduler;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.swengineer.habit.entity.Habit;
import org.swengineer.habit.entity.enums.FrequencyType;
import org.swengineer.habit.entity.enums.HabitCategory;
import org.swengineer.habit.repository.HabitRepository;
import org.swengineer.user.entity.User;
import org.swengineer.user.repository.UserRepository;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
@ActiveProfiles("test")
//@Disabled("실제 OpenAI API 키 필요 - 수동으로만 실행")
class AiCoachingRealTest {

    @Autowired
    MorningCoachingScheduler morningCoachingScheduler;

    @Autowired
    UserRepository userRepository;

    @Autowired
    HabitRepository habitRepository;

    @Test
    void 실제_GPT_호출_테스트() {
        // routinerId, nickname, password 순서
        User user = User.create("test123", "테스트유저", "1234");
        userRepository.save(user);

        Set<DayOfWeek> allDays = new HashSet<>(Arrays.asList(DayOfWeek.values()));

        Habit habit = Habit.create(
                user.getId(),
                "운동",
                HabitCategory.HEALTH,
                FrequencyType.DAILY,
                allDays
        );
        habitRepository.save(habit);

        morningCoachingScheduler.generateMorningCoachingMessages();
    }
}