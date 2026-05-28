package org.swengineer.stats.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swengineer.global.api.response.dto.ApiResponse;
import org.swengineer.global.api.response.dto.SuccessResponse;
import org.swengineer.stats.code.StatsSuccessCode;
import org.swengineer.stats.dto.response.StatsDashboardResponse;
import org.swengineer.stats.service.StatsService;

@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @Operation (summary = "통계 대시보드 조회")
    @GetMapping("/dashboard")
    public ResponseEntity<SuccessResponse<StatsDashboardResponse>> getDashboard(
            @AuthenticationPrincipal Long userId
    ) {
        StatsDashboardResponse response = statsService.getDashboard(userId);
        return ResponseEntity.ok(ApiResponse.success(StatsSuccessCode.STATS_DASHBOARD_SUCCESS, response));
    }
}
