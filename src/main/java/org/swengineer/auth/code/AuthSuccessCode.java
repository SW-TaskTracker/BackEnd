package org.swengineer.auth.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.swengineer.global.api.code.SuccessResultCode;

@Getter
@RequiredArgsConstructor
public enum AuthSuccessCode implements SuccessResultCode {

    SIGNUP_SUCCESS(HttpStatus.CREATED, "회원가입이 완료되었습니다."),
    LOGIN_SUCCESS(HttpStatus.OK, "로그인 성공"),
    ;

    private final HttpStatus status;
    private final String message;
}
