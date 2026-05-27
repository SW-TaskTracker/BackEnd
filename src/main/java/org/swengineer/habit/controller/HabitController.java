package org.swengineer.habit.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.swengineer.global.api.response.dto.ApiResponse;
import org.swengineer.global.api.response.dto.SuccessResponse;
import org.swengineer.habit.code.HabitSuccessCode;
import org.swengineer.habit.dto.request.CreateHabitRequest;
import org.swengineer.habit.dto.response.HabitListResponse;
import org.swengineer.habit.dto.response.HabitResponse;
import org.swengineer.habit.dto.response.TodayHabitResponse;
import org.swengineer.habit.entity.enums.HabitCategory;
import org.swengineer.habit.service.HabitService;

import java.time.DayOfWeek;
import java.util.List;

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

    @Operation(summary = "오늘의 습관 조회 (메인 홈)",
            description = "오늘 요일에 해당하는 습관 목록 + 달성률 + 최고 스트릭을 조회합니다.")
    @GetMapping("/today")
    public ResponseEntity<SuccessResponse<TodayHabitResponse>> getTodayHabits(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false)
            @Schema(description = "카테고리 필터")
            HabitCategory category) {
        TodayHabitResponse response = habitService.getTodayHabits(userId,category);
        return ResponseEntity.ok(ApiResponse.success(HabitSuccessCode.HABIT_LIST_SUCCESS, response));
    }

    @Operation(summary = "습관 목록 조회 (전체보기)",
            description = """
                    요일별/카테고리별 필터링 + 검색이 가능합니다.
                    - dayOfWeek: 미입력 시 오늘 요일 기준
                    - category: 미입력 시 전체
                    - keyword: 습관 이름 검색
                    """)
    @GetMapping
    public ResponseEntity<SuccessResponse<List<HabitListResponse>>> getHabits(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false)
            @Schema(description = "요일 필터", example = "MONDAY")
            DayOfWeek dayOfWeek,
            @RequestParam(required = false)
            @Schema(description = "카테고리 필터")
            HabitCategory category,
            @RequestParam(required = false)
            @Schema(description = "습관 이름 검색")
            String keyword) {
        List<HabitListResponse> response = habitService.getHabits(userId, dayOfWeek, category, keyword);
        return ResponseEntity.ok(ApiResponse.success(HabitSuccessCode.HABIT_LIST_SUCCESS, response));
    }
}