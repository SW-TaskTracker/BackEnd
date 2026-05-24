package org.swengineer.checkin.controller;

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
    @DeleteMapping("/{checkInId}")
    public ResponseEntity<SuccessResponse<Void>> cancel(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long checkInId
    ) {
        checkInService.cancel(userId, checkInId);
        return ResponseEntity.ok(ApiResponse.success(CheckInSuccessCode.CHECK_IN_CANCEL_SUCCESS));
    }
}
