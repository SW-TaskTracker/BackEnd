package org.swengineer.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.swengineer.ai.client.OpenAiApiCaller;
import org.swengineer.ai.dto.context.MorningCoachingContext;
import org.swengineer.ai.dto.request.Message;
import org.swengineer.ai.dto.response.CoachingMessageResponse;
import org.swengineer.ai.fallback.FallbackMessageProvider;
import org.swengineer.ai.repository.CoachingMessageRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiCoachingService {

    private final OpenAiApiCaller openAiApiCaller;
    private final FallbackMessageProvider fallbackMessageProvider;
    private final CoachingMessageRepository coachingMessageRepository;

    private static final String SYSTEM_PROMPT =
            """
         당신은 습관 관리 앱의 아침 코칭 AI입니다.
        다음 규칙을 반드시 지켜주세요:
        - 글자 수는 공백 포함 150자 이내
        - 이모지는 최대 2개까지만 사용
        - 문장은 반드시 3줄 이내, 줄바꿈(\\n)을 포함할 것
        - 친근하고 따뜻한 한국어 반말 또는 존댓말 톤 유지
        - 오직 메시지 본문만 출력, 부가 설명 없음
        
        """;

    public String generatingMorningMessage(MorningCoachingContext context){

    try{
        String userPrompt = builderUserPrompt(context);

        List <Message> messages = List.of(
                new Message("system",SYSTEM_PROMPT)
                ,new Message("user",userPrompt)
        );

        String result = openAiApiCaller.callMessages(messages);
        return validateAndTrim(result);
    }
    catch(Exception e)
    {
        log.warn("AI 코칭 메시지 생성 실패, fallback 사용: {}", e.getMessage());
        return fallbackMessageProvider.getRandom();
    }

}

private String builderUserPrompt(MorningCoachingContext context) {
    return String.format(
            """
                    사용자 정보:
                                - 현재 스트릭: %d일 연속
                                - 오늘 해야 할 습관: %s
                                - 전체 달성률: %.0f%%
                               \s
                                위 정보를 바탕으로 오늘 아침 응원/리마인더 메시지를 생성해주세요.        
                    """,
            context.currentStreak(),
            String.join(", ",context.todayHabits()),
            context.achievementRate()
    );}

    private String validateAndTrim(String message) {
        if (message == null || message.isBlank()) {
            return fallbackMessageProvider.getRandom();
        }
        // 150자 초과 시 자르기 (안전장치)
        return message.length() > 150 ? message.substring(0, 150) : message;
    }

    public CoachingMessageResponse getCoachingMessage(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime todayAt7 = today.atTime(7, 0);
        LocalDateTime tomorrowAt7 = today.plusDays(1).atTime(7, 0);

        if (LocalDateTime.now().isBefore(todayAt7)) {
            todayAt7 = today.minusDays(1).atTime(7, 0);
            tomorrowAt7 = today.atTime(7, 0);
        }

        return coachingMessageRepository
                .findTopByUserIdAndGeneratedAtBetweenOrderByGeneratedAtDesc(
                        userId, todayAt7, tomorrowAt7
                )
                .map(msg -> CoachingMessageResponse.of(msg.getContent(), msg.getGeneratedAt()))
                .orElse(CoachingMessageResponse.empty());
    }
}


