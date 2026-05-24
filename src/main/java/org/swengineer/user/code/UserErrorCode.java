package org.swengineer.user.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.swengineer.global.api.code.ErrorResultCode;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorResultCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    ALREADY_WITHDRAWN(HttpStatus.GONE, "이미 탈퇴한 사용자입니다."),
    ;

    private final HttpStatus status;
    private final String message;
}
