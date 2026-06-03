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

        if (date.isAfter(today)) {
            throw new CustomException(HistoryErrorCode.FUTURE_DATE_NOT_ALLOWED);
        }

        LocalDateTime dayStart = date.atStartOfDay();       // 00:00:00 - deletedAt 비교용
        LocalDateTime dayEnd = date.atTime(23, 59, 59);     // 23:59:59 - createdAt 비교용

        List<Habit> habits = habitRepository.findHabitsActiveOnDate(
                userId, date.getDayOfWeek(), dayStart, dayEnd);

        Map<Long, CheckIn> completedCheckIns = checkInRepository
                .findCompletedCheckInsByDate(userId, date)
                .stream()
                .collect(Collectors.toMap(CheckIn::getHabitId, c -> c));

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
