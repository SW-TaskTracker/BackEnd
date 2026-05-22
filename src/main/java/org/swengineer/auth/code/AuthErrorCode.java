package org.swengineer.auth.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.swengineer.global.api.code.ErrorResultCode;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorResultCode {

    DUPLICATE_ROUTINER_ID(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    ;

    private final HttpStatus status;
    private final String message;
}