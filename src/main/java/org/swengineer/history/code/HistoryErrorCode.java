package org.swengineer.history.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.swengineer.global.api.code.ErrorResultCode;

@Getter
@RequiredArgsConstructor
public enum HistoryErrorCode implements ErrorResultCode {

    FUTURE_DATE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "오늘 이후 날짜는 조회할 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String message;
}
