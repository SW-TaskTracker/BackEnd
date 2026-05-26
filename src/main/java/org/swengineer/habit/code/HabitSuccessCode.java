package org.swengineer.habit.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.swengineer.global.api.code.SuccessResultCode;

@Getter
@RequiredArgsConstructor
public enum HabitSuccessCode implements SuccessResultCode {
    HABIT_CREATED(HttpStatus.CREATED, "습관이 등록되었습니다.");

    private final HttpStatus Status;
    private final String message;
}