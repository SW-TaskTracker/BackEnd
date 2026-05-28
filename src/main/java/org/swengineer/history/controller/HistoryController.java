package org.swengineer.history.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.swengineer.global.api.response.dto.ApiResponse;
import org.swengineer.global.api.response.dto.SuccessResponse;
import org.swengineer.history.code.HistorySuccessCode;
import org.swengineer.history.dto.response.DayHistoryResponse;
import org.swengineer.history.service.HistoryService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @Operation(
            summary = "날짜별 히스토리 조회",
            description = """
                    캘린더에서 선택한 날짜의 습관 목록과 완료 여부를 조회합니다.
                    - 오늘 이후 날짜는 조회 불가 (400 반환)
                    - 그날 당시 활성화되어 있던 습관만 포함됩니다 (이후 삭제된 습관도 포함)
                    - 완료한 습관은 completed=true + checkedAtKst(HH:mm) 반환
                    - 미완료 습관은 completed=false + checkedAtKst=null 반환
                    """
    )
    @GetMapping
    public ResponseEntity<SuccessResponse<DayHistoryResponse>> getHistory(
            @AuthenticationPrincipal Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        DayHistoryResponse response = historyService.getHistory(userId, date);
        return ResponseEntity.ok(ApiResponse.success(HistorySuccessCode.HISTORY_SUCCESS, response));
    }
}
