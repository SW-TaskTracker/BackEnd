package org.swengineer.history.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.swengineer.global.api.code.SuccessResultCode;

@Getter
@RequiredArgsConstructor
public enum HistorySuccessCode implements SuccessResultCode {

    HISTORY_SUCCESS(HttpStatus.OK, "히스토리 조회 성공"),
    ;

    private final HttpStatus status;
    private final String message;
}
