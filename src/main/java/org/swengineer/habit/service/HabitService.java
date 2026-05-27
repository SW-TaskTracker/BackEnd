package org.swengineer.habit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swengineer.checkin.repository.CheckInRepository;
import org.swengineer.global.api.exception.CustomException;
import org.swengineer.habit.code.HabitErrorCode;
import org.swengineer.habit.dto.request.CreateHabitRequest;
import org.swengineer.habit.dto.request.UpdateHabitRequest;
import org.swengineer.habit.dto.response.HabitListResponse;
import org.swengineer.habit.dto.response.HabitResponse;
import org.swengineer.habit.dto.response.TodayHabitResponse;
import org.swengineer.habit.entity.Habit;
import org.swengineer.habit.entity.HabitRecord;
import org.swengineer.habit.entity.enums.FrequencyType;
import org.swengineer.habit.entity.enums.HabitCategory;
import org.swengineer.habit.repository.HabitRecordRepository;
import org.swengineer.habit.repository.HabitRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HabitService {

    private static final int MAX_ACTIVE_HABITS = 10;
    private final HabitRepository habitRepository;
    private final HabitRecordRepository habitRecordRepository;
    private final CheckInRepository checkInRepository;

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
    public TodayHabitResponse getTodayHabits(Long userId,HabitCategory category) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        DayOfWeek todayDow = today.getDayOfWeek();

        List<Habit> habits;
        if (category != null) {
            habits = habitRepository.findByUserIdAndDayOfWeekAndCategory(userId, todayDow, category);
        } else {
            habits = habitRepository.findByUserIdAndDayOfWeek(userId, todayDow);
        }

        List<HabitListResponse> responses = habits.stream()
                .map(habit -> toHabitListResponse(habit, userId, today))
                .toList();

        return TodayHabitResponse.of(responses);
    }

    // ========== 습관 목록: 필터 + 검색 ==========
    public List<HabitListResponse> getHabits(Long userId, DayOfWeek dayOfWeek,
                                             HabitCategory category, String keyword) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        if (dayOfWeek == null) {
            dayOfWeek = today.getDayOfWeek();
        }

        List<Habit> habits;
        if (category != null) {
            habits = habitRepository.findByUserIdAndDayOfWeekAndCategory(userId, dayOfWeek, category);
        } else {
            habits = habitRepository.findByUserIdAndDayOfWeek(userId, dayOfWeek);
        }

        if (keyword != null && !keyword.isBlank()) {
            String search = keyword.trim().toLowerCase();
            habits = habits.stream()
                    .filter(h -> h.getName().toLowerCase().contains(search))
                    .toList();
        }

        return habits.stream()
                .map(habit -> toHabitListResponse(habit, userId, today))
                .toList();
    }

    // ========== 공통: 응답 변환 ==========
    private HabitListResponse toHabitListResponse(Habit habit, Long userId, LocalDate today) {
        boolean completedToday = checkInRepository
                .findTodayCheckIn(userId, habit.getId(), today)
                .isPresent();
        int streak = calculateStreak(habit, userId, today);
        return HabitListResponse.of(habit, completedToday, streak);
    }

    // ========== 연속 기록 계산 ==========
    private int calculateStreak(Habit habit, Long userId, LocalDate today) {
        LocalDate date = today;
        int streak = 0;

        boolean completedToday = checkInRepository
                .findTodayCheckIn(userId, habit.getId(), date)
                .isPresent();
        if (!completedToday) {
            date = date.minusDays(1);
        }

        while (streak <= 365) {
            if (habit.getCustomDays().contains(date.getDayOfWeek())) {
                boolean done = checkInRepository
                        .findTodayCheckIn(userId, habit.getId(), date)
                        .isPresent();
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

    @Transactional
    public HabitResponse updateHabit(Long userId, Long habitId, UpdateHabitRequest request) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new CustomException(HabitErrorCode.HABIT_NOT_FOUND));

        if (!habit.getUserId().equals(userId)) {
            throw new CustomException(HabitErrorCode.HABIT_NOT_FOUND);
        }

        if (habit.getDeletedAt() != null) {
            throw new CustomException(HabitErrorCode.HABIT_NOT_FOUND);
        }

        if (request.frequencyType() == FrequencyType.CUSTOM) {
            if (request.customDays() == null || request.customDays().isEmpty()) {
                throw new CustomException(HabitErrorCode.CUSTOM_DAYS_REQUIRED);
            }
        }

        habit.update(request.name(), request.category(),
                request.frequencyType(), request.customDays());

        return HabitResponse.from(habit);
    }

    @Transactional
    public void deleteHabit(Long userId, Long habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new CustomException(HabitErrorCode.HABIT_NOT_FOUND));

        if (!habit.getUserId().equals(userId)) {
            throw new CustomException(HabitErrorCode.HABIT_NOT_FOUND);
        }

        if (habit.getDeletedAt() != null) {
            throw new CustomException(HabitErrorCode.HABIT_NOT_FOUND);
        }

        habit.softDelete();
    }

    @Transactional
    public boolean toggleHabitRecord(Long userId, Long habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new CustomException(HabitErrorCode.HABIT_NOT_FOUND));

        if (!habit.getUserId().equals(userId)) {
            throw new CustomException(HabitErrorCode.HABIT_NOT_FOUND);
        }

        LocalDate today = LocalDate.now();

        // 오늘 해당하는 습관인지 확인
        if (!habit.getCustomDays().contains(today.getDayOfWeek())) {
            throw new CustomException(HabitErrorCode.NOT_TODAY_HABIT);
        }

        boolean exists = habitRecordRepository.existsByHabitIdAndRecordDate(habitId, today);

        if (exists) {
            // 취소: 당일 23:59까지만 가능
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime deadline = today.atTime(23, 59, 59);
            if (now.isAfter(deadline)) {
                throw new CustomException(HabitErrorCode.CANCEL_TIME_EXPIRED);
            }
            habitRecordRepository.deleteByHabitIdAndRecordDate(habitId, today);
            return false;
        } else {
            // 체크인: 중복 방지 (unique constraint + 코드 레벨 검증)
            habitRecordRepository.save(HabitRecord.create(habit, today));
            return true;
        }
    }
}