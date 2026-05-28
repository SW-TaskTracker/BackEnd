package org.swengineer.checkin.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.swengineer.checkin.code.CheckInSuccessCode;
import org.swengineer.checkin.dto.request.CheckInRequest;
import org.swengineer.checkin.dto.response.CheckInResponse;
import org.swengineer.checkin.service.CheckInService;
import org.swengineer.global.api.response.dto.ApiResponse;
import org.swengineer.global.api.response.dto.SuccessResponse;

@RestController
@RequestMapping("/api/v1/check-in")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;

    //체크인 처리

    @Operation(summary = "습관 체크인 토글",
            description = """
                오늘 날짜 기준으로 습관 체크인/취소를 토글합니다.
                - 오늘 요일에 해당하는 습관만 체크인 가능
                - 체크인 취소는 당일 23:59까지만 가능
                - 중복 체크인 불가
                - 체크인 시간이 자동 저장됩니다 (AI 분석용)
                """)
    @PostMapping
    public ResponseEntity<SuccessResponse<CheckInResponse>> checkIn(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CheckInRequest request
    ) {
        CheckInResponse response = checkInService.checkIn(userId, request.habitId());
        return ResponseEntity
                .status(201)
                .body(ApiResponse.success(CheckInSuccessCode.CHECK_IN_SUCCESS, response));
    }

    //체크인 취소

    @Operation(summary = "습관 체크인 취소",
            description = """
            체크인을 취소합니다.
            - 당일 23:59까지만 취소 가능
            """)
    @DeleteMapping("/{checkInId}")
    public ResponseEntity<SuccessResponse<Void>> cancel(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long checkInId
    ) {
        checkInService.cancel(userId, checkInId);
        return ResponseEntity.ok(ApiResponse.success(CheckInSuccessCode.CHECK_IN_CANCEL_SUCCESS));
    }
}
