package org.swengineer.history.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swengineer.checkin.entity.CheckIn;
import org.swengineer.checkin.repository.CheckInRepository;
import org.swengineer.global.api.exception.CustomException;
import org.swengineer.habit.entity.Habit;
import org.swengineer.habit.repository.HabitRepository;
import org.swengineer.history.code.HistoryErrorCode;
import org.swengineer.history.dto.response.DayHistoryResponse;
import org.swengineer.history.dto.response.HabitHistoryItemResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HistoryService {

    private final HabitRepository habitRepository;
    private final CheckInRepository checkInRepository;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public DayHistoryResponse getHistory(Long userId, LocalDate date) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        // 오늘 이후 날짜 조회 불가
        if (date.isAfter(today)) {
            throw new CustomException(HistoryErrorCode.FUTURE_DATE_NOT_ALLOWED);
        }

        // 선택한 날짜의 끝 시각 (23:59:59) 기준으로 활성 습관 조회
        // → 그날 중에 생성된 습관도 포함하기 위해 당일 말일 기준으로 조회
        LocalDateTime targetDateTime = date.atTime(23, 59, 59);

        // 그날 요일에 해당하고, 그날 당시 활성이었던 습관 목록
        List<Habit> habits = habitRepository.findHabitsActiveOnDate(
                userId,
                date.getDayOfWeek(),
                targetDateTime
        );

        // 그날 완료된 체크인 조회 (habitId → CheckIn 매핑)
        Map<Long, CheckIn> completedCheckIns = checkInRepository
                .findCompletedCheckInsByDate(userId, date)
                .stream()
                .collect(Collectors.toMap(CheckIn::getHabitId, c -> c));

        // 습관별 완료 여부 + 체크인 시각 매핑
        List<HabitHistoryItemResponse> habitItems = habits.stream()
                .map(habit -> {
                    CheckIn checkIn = completedCheckIns.get(habit.getId());
                    boolean completed = checkIn != null;
                    String checkedAtKst = completed
                            ? checkIn.getCheckedAtKst().format(TIME_FORMATTER)
                            : null;
                    return HabitHistoryItemResponse.of(habit, completed, checkedAtKst);
                })
                .toList();

        return DayHistoryResponse.of(date, habitItems);
    }
}
