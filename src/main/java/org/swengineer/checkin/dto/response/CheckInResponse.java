package org.swengineer.checkin.dto.response;


import org.swengineer.checkin.entity.CheckIn;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CheckInResponse(
        Long checkInId,
        Long habitId,
        LocalDateTime checkedAtKst,
        DayOfWeek dayOfWeek,
        LocalDate checkedDate
) {
    public static CheckInResponse from(CheckIn checkIn) {
        return new CheckInResponse(
                checkIn.getId(),
                checkIn.getHabitId(),
                checkIn.getCheckedAtKst(),
                checkIn.getDayOfWeek(),
                checkIn.getCheckedDate()
        );
    }
}