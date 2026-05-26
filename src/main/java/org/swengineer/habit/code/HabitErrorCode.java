package org.swengineer.habit.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.swengineer.global.api.code.ErrorResultCode;

@Getter
@RequiredArgsConstructor
public enum HabitErrorCode implements ErrorResultCode {
    HABIT_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "습관은 최대 10개까지 등록할 수 있습니다."),
    CUSTOM_DAYS_REQUIRED(HttpStatus.BAD_REQUEST, "요일 선택 시 최소 1개의 요일을 선택해야 합니다."),
    HABIT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 습관을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}