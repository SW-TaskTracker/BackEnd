package org.swengineer.habit.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swengineer.global.api.response.dto.ApiResponse;
import org.swengineer.global.api.response.dto.SuccessResponse;
import org.swengineer.habit.code.HabitSuccessCode;
import org.swengineer.habit.dto.request.CreateHabitRequest;
import org.swengineer.habit.dto.response.HabitResponse;
import org.swengineer.habit.service.HabitService;

@RestController
@RequestMapping("/api/v1/habits")
@RequiredArgsConstructor
public class HabitController {

    private final HabitService habitService;

    @Operation(summary = "습관 등록",
            description = """
                    새로운 습관을 등록합니다.
                    - frequencyType이 DAILY이면 customDays는 null로 보내주세요 (자동으로 월~일 설정됨)
                    - frequencyType이 CUSTOM이면 customDays에 최소 1개 요일을 선택해주세요
                    - 유저당 활성 습관은 최대 10개까지 등록 가능합니다
                    """)
    @PostMapping
    public ResponseEntity<SuccessResponse<HabitResponse>> createHabit(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody CreateHabitRequest request) {
        HabitResponse response = habitService.createHabit(userId, request);
        return ResponseEntity.ok(ApiResponse.success(HabitSuccessCode.HABIT_CREATED, response));
    }
}