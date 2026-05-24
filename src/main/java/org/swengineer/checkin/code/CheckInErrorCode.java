package org.swengineer.checkin.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.swengineer.global.api.code.ErrorResultCode;

@Getter
@RequiredArgsConstructor
public enum CheckInErrorCode implements ErrorResultCode {

    CHECK_IN_NOT_FOUND(HttpStatus.NOT_FOUND, "체크인을 찾을 수 없습니다."),
    CHECK_IN_ALREADY_EXISTS(HttpStatus.CONFLICT, "오늘 이미 체크인한 습관입니다."),
    CHECK_IN_ALREADY_CANCELED(HttpStatus.CONFLICT, "이미 취소된 체크인입니다."),
    CHECK_IN_FORBIDDEN(HttpStatus.FORBIDDEN, "본인의 체크인만 취소할 수 있습니다.");

    private final HttpStatus status;
    private final String message;
}
