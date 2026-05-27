package org.swengineer.habit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swengineer.global.api.exception.CustomException;
import org.swengineer.habit.code.HabitErrorCode;
import org.swengineer.habit.dto.request.CreateHabitRequest;
import org.swengineer.habit.dto.response.HabitListResponse;
import org.swengineer.habit.dto.response.HabitResponse;
import org.swengineer.habit.dto.response.TodayHabitResponse;
import org.swengineer.habit.entity.Habit;
import org.swengineer.habit.entity.enums.FrequencyType;
import org.swengineer.habit.entity.enums.HabitCategory;
import org.swengineer.habit.repository.HabitRecordRepository;
import org.swengineer.habit.repository.HabitRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HabitService {

    private static final int MAX_ACTIVE_HABITS = 10;
    private final HabitRepository habitRepository;
    private final HabitRecordRepository habitRecordRepository;

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

    // ========== 메인 홈: 오늘의 습관 ==========
    public TodayHabitResponse getTodayHabits(Long userId) {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        List<Habit> habits = habitRepository.findByUserIdAndDayOfWeek(userId, today);

        List<HabitListResponse> responses = habits.stream()
                .map(habit -> toHabitListResponse(habit))
                .toList();

        return TodayHabitResponse.of(responses);
    }

    // ========== 습관 목록: 필터 + 검색 ==========
    public List<HabitListResponse> getHabits(Long userId, DayOfWeek dayOfWeek,
                                             HabitCategory category, String keyword) {
        if (dayOfWeek == null) {
            dayOfWeek = LocalDate.now().getDayOfWeek();
        }

        List<Habit> habits;
        if (category != null) {
            habits = habitRepository.findByUserIdAndDayOfWeekAndCategory(userId, dayOfWeek, category);
        } else {
            habits = habitRepository.findByUserIdAndDayOfWeek(userId, dayOfWeek);
        }

        // 검색어 필터
        if (keyword != null && !keyword.isBlank()) {
            String search = keyword.trim().toLowerCase();
            habits = habits.stream()
                    .filter(h -> h.getName().toLowerCase().contains(search))
                    .toList();
        }

        return habits.stream()
                .map(habit -> toHabitListResponse(habit))
                .toList();
    }

    // ========== 공통: 응답 변환 ==========
    private HabitListResponse toHabitListResponse(Habit habit) {
        LocalDate today = LocalDate.now();
        boolean completedToday = habitRecordRepository
                .existsByHabitIdAndRecordDate(habit.getId(), today);
        int streak = calculateStreak(habit);
        return HabitListResponse.of(habit, completedToday, streak);
    }

    // ========== 연속 기록 계산 ==========
    private int calculateStreak(Habit habit) {
        LocalDate date = LocalDate.now();
        int streak = 0;

        boolean completedToday = habitRecordRepository
                .existsByHabitIdAndRecordDate(habit.getId(), date);
        if (!completedToday) {
            date = date.minusDays(1);
        }

        while (streak <= 365) {
            if (habit.getCustomDays().contains(date.getDayOfWeek())) {
                boolean done = habitRecordRepository
                        .existsByHabitIdAndRecordDate(habit.getId(), date);
                if (done) {
                    streak++;
                } else {
                    break;
                }
            }
            date = date.minusDays(1);
        }

        return streak;
    }
}