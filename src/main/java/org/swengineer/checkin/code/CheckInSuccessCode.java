package org.swengineer.checkin.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.swengineer.global.api.code.SuccessResultCode;

@Getter
@RequiredArgsConstructor
public enum CheckInSuccessCode  implements SuccessResultCode {


    CHECK_IN_SUCCESS(HttpStatus.CREATED, "체크인이 완료되었습니다."),
    CHECK_IN_CANCEL_SUCCESS(HttpStatus.OK, "체크인이 취소되었습니다."),
    CHECK_IN_LIST_SUCCESS(HttpStatus.OK, "체크인 목록 조회에 성공했습니다.");


    private final HttpStatus status;
    private final String message;
}
