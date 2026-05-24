package org.swengineer.user.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.swengineer.global.api.code.SuccessResultCode;

@Getter
@RequiredArgsConstructor
public enum UserSuccessCode implements SuccessResultCode {

    GET_PROFILE_SUCCESS(HttpStatus.OK, "프로필 조회 성공"),
    UPDATE_NICKNAME_SUCCESS(HttpStatus.OK, "닉네임이 변경되었습니다."),
    WITHDRAW_SUCCESS(HttpStatus.OK, "회원탈퇴가 완료되었습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK, "로그아웃 되었습니다."),
    ;

    private final HttpStatus status;
    private final String message;
}
