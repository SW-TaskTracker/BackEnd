package org.swengineer.stats.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.swengineer.global.api.code.SuccessResultCode;

@Getter
@RequiredArgsConstructor
public enum StatsSuccessCode implements SuccessResultCode {

    STATS_DASHBOARD_SUCCESS(HttpStatus.OK, "통계 대시보드 조회 성공"),
    ;

    private final HttpStatus status;
    private final String message;
}
