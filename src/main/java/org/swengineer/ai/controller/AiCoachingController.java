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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.swengineer.ai.entity.CoachingMessage;
import org.swengineer.ai.repository.CoachingMessageRepository;
import org.swengineer.ai.scheduler.MorningCoachingScheduler;

import java.util.Map;
import java.util.Optional;


@Tag(name = "AI 코칭", description = "매일 아침 AI가 생성하는 동기부여 코칭 메시지 API")
@RestController
@RequestMapping("/api/v1/ai/coaching")
@RequiredArgsConstructor
public class AiCoachingController {

    private final CoachingMessageRepository coachingMessageRepository;
    private final MorningCoachingScheduler morningCoachingScheduler;

    @Operation(
            summary = "코칭 메시지 조회",
            description = "가장 최근에 생성된 AI 코칭 메시지를 반환합니다. 메시지는 매일 아침 7시에 자동 생성됩니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공 (메시지 없을 경우 hasMessage: false 반환)"),
            @ApiResponse(responseCode = "401", description = "인증 실패 - JWT 토큰 없음 또는 만료"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping
    public ResponseEntity<?> getCoachingMessage(@AuthenticationPrincipal Long userId) {
        Optional<CoachingMessage> message = coachingMessageRepository
                .findTopByUserIdOrderByGeneratedAtDesc(userId);

        if (message.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "hasMessage", false,
                    "message", "아직 코칭 메시지가 없어요! 내일 아침 7시에 첫 메시지가 도착할 거예요 🌅"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "hasMessage", true,
                "content", message.get().getContent(),
                "generatedAt", message.get().getGeneratedAt()
        ));
    }


    @Operation(
            summary = "[개발용] 코칭 메시지 수동 생성",
            description = "스케줄러를 즉시 실행하여 모든 유저의 코칭 메시지를 강제 생성합니다. 개발/테스트 환경에서만 사용하세요."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메시지 생성 완료")
    })
    // 테스트용 수동 트리거 (개발 환경에서만 사용)
    @PostMapping("/trigger")
    public ResponseEntity<?> triggerCoaching() {
        morningCoachingScheduler.generateMorningCoachingMessages();
        return ResponseEntity.ok(Map.of("message", "코칭 메시지 생성 완료"));
    }
}
