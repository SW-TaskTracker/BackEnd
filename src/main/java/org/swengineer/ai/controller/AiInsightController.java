package org.swengineer.ai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swengineer.ai.entity.AiInsight;
import org.swengineer.ai.repository.AiInsightRepository;
import org.swengineer.checkin.repository.CheckInRepository;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;


@Tag(name = "AI 인사이트", description = "주간 체크인 데이터를 분석하여 AI가 생성하는 습관 패턴 인사이트 API")
@RestController
@RequestMapping("/api/v1/ai/insights")
@RequiredArgsConstructor
public class AiInsightController {

    private final AiInsightRepository aiInsightRepository;
    private final CheckInRepository checkInRepository;

    @Operation(
            summary = "AI 인사이트 조회",
            description = """
            최근 7일간 체크인 데이터를 기반으로 AI가 분석한 습관 패턴 인사이트를 반환합니다.
            
            응답 type 종류:
            - COLD_START: 데이터 부족 (7일 미만)
            - PENDING: 데이터는 충분하나 아직 인사이트 미생성 (매주 일요일 밤 업데이트)
            - INSIGHT: 정상 인사이트 반환
            """,
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공 (type 필드로 상태 구분)"),
            @ApiResponse(responseCode = "401", description = "인증 실패 - JWT 토큰 없음 또는 만료"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping
    public ResponseEntity<?> getInsight(@AuthenticationPrincipal Long userId) {

        // 7일 이상 데이터 있는지 확인
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        int checkInCount = checkInRepository
                .countByUserIdAndCheckedDateAfter(userId, sevenDaysAgo);

        // Cold Start: 7일 미만
        if (checkInCount < 1) {
            return ResponseEntity.ok(Map.of(
                    "hasInsight", false,
                    "type", "COLD_START",
                    "message", "아직 데이터가 부족해요! 일주일 동안 습관을 쌓으면 AI가 패턴을 분석해 드릴게요"
            ));
        }

        // 저장된 인사이트 조회
        Optional<AiInsight> insight = aiInsightRepository
                .findTopByUserIdOrderByGeneratedAtDesc(userId);

        // 인사이트 없음 (아직 배치 안 돌았을 때)
        if (insight.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "hasInsight", false,
                    "type", "PENDING",
                    "message", "곧 첫 번째 인사이트가 도착할 예정이에요! 매주 일요일 밤에 업데이트돼요 🌙"
            ));
        }

        // 정상 인사이트 반환
        return ResponseEntity.ok(Map.of(
                "hasInsight", true,
                "type", "INSIGHT",
                "content", insight.get().getContent(),
                "generatedAt", insight.get().getGeneratedAt()
        ));
    }
}