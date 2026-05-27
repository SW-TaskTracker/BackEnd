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
import org.swengineer.habit.dto.request.UpdateHabitRequest;
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

    @Operation(summary = "습관 수정",
            description = """
                습관의 이름, 카테고리, 빈도를 수정합니다.
                - 과거 체크인 데이터는 보존됩니다
                """)
    @PutMapping("/{habitId}")
    public ResponseEntity<SuccessResponse<HabitResponse>> updateHabit(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long habitId,
            @Valid @RequestBody UpdateHabitRequest request) {
        HabitResponse response = habitService.updateHabit(userId, habitId, request);
        return ResponseEntity.ok(ApiResponse.success(HabitSuccessCode.HABIT_UPDATED, response));
    }

    @Operation(summary = "습관 삭제",
            description = """
                습관을 soft delete 처리합니다.
                - 과거 체크인 기록은 보존됩니다
                - 오늘 이후 메인 목록에서 제외됩니다
                """)
    @DeleteMapping("/{habitId}")
    public ResponseEntity<SuccessResponse<Void>> deleteHabit(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long habitId) {
        habitService.deleteHabit(userId, habitId);
        return ResponseEntity.ok(ApiResponse.success(HabitSuccessCode.HABIT_DELETED));
    }

    /*
    @Operation(summary = "습관 체크인 토글",
            description = """
                오늘 날짜 기준으로 습관 체크인/취소를 토글합니다.
                - 오늘 요일에 해당하는 습관만 체크인 가능
                - 체크인 취소는 당일 23:59까지만 가능
                - 중복 체크인 불가
                - 체크인 시간이 자동 저장됩니다 (AI 분석용)
                """)
    @PostMapping("/{habitId}/check")
    public ResponseEntity<SuccessResponse<Boolean>> toggleHabit(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long habitId) {
        boolean completed = habitService.toggleHabitRecord(userId, habitId);
        return ResponseEntity.ok(ApiResponse.success(HabitSuccessCode.HABIT_TOGGLE_SUCCESS, completed));
    }

     */
}