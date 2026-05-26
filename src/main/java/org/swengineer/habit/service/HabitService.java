package org.swengineer.habit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swengineer.global.api.exception.CustomException;
import org.swengineer.habit.code.HabitErrorCode;
import org.swengineer.habit.dto.request.CreateHabitRequest;
import org.swengineer.habit.dto.response.HabitResponse;
import org.swengineer.habit.entity.Habit;
import org.swengineer.habit.entity.enums.FrequencyType;
import org.swengineer.habit.repository.HabitRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HabitService {

    private static final int MAX_ACTIVE_HABITS = 10;
    private final HabitRepository habitRepository;

    @Transactional
    public HabitResponse createHabit(Long userId, CreateHabitRequest request) {
        // 활성 습관 개수 제한
        int activeCount = habitRepository.countByUserIdAndDeletedAtIsNull(userId);
        if (activeCount >= MAX_ACTIVE_HABITS) {
            throw new CustomException(HabitErrorCode.HABIT_LIMIT_EXCEEDED);
        }

        // CUSTOM일 때 요일 최소 1개 검증
        if (request.frequencyType() == FrequencyType.CUSTOM) {
            if (request.customDays() == null || request.customDays().isEmpty()) {
                throw new CustomException(HabitErrorCode.CUSTOM_DAYS_REQUIRED);
            }
        }

        Habit habit = Habit.create(
                userId,
                request.name(),
                request.category(),
                request.frequencyType(),
                request.customDays()
        );

        habitRepository.save(habit);
        return HabitResponse.from(habit);
    }
}