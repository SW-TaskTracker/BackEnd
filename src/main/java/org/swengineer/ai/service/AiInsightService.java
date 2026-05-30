package org.swengineer.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.swengineer.ai.client.OpenAiApiCaller;
import org.swengineer.ai.dto.request.Message;
import org.swengineer.ai.fallback.FallbackInsightProvider;
import org.swengineer.checkin.entity.CheckIn;
import org.swengineer.checkin.repository.CheckInRepository;
import org.swengineer.habit.repository.HabitRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiInsightService {

    private final OpenAiApiCaller openAiApiCaller;
    private final FallbackInsightProvider fallbackMessageProvider;
    private final CheckInRepository checkInRepository;
    private final HabitRepository habitRepository;

    private static final String SYSTEM_PROMPT = """
            당신은 습관 관리 앱의 패턴 분석 AI입니다.
            다음 규칙을 반드시 지켜주세요:
            - 유저의 이름을 친근하게 부르며 시작
            - 구체적인 요일/시간대 패턴을 언급
            - 200자 이내
            - 따뜻하고 격려하는 톤
            - 오직 인사이트 본문만 출력
            """;

    public String generateInsight(Long userId) {
        try {
            String userPrompt = buildInsightPrompt(userId);

            List<Message> messages = List.of(
                    new Message("system", SYSTEM_PROMPT),
                    new Message("user", userPrompt)
            );

            return openAiApiCaller.callMessages(messages);

        } catch (Exception e) {
            log.warn("인사이트 생성 실패, fallback 사용: {}", e.getMessage());
            return fallbackMessageProvider.getRandom();
        }
    }

    private String buildInsightPrompt(Long userId) {
        // 최근 4주 체크인 데이터 조회
        LocalDate fourWeeksAgo = LocalDate.now().minusWeeks(4);
        List<CheckIn> checkIns = checkInRepository
                .findByUserIdAndCheckedDateAfter(userId, fourWeeksAgo);

        // 요일별 달성/미달성 패턴 계산
        Map<DayOfWeek, Long> completedByDay = checkIns.stream()
                .filter(c -> !c.isCanceled())
                .collect(Collectors.groupingBy(
                        c -> c.getCheckedDate().getDayOfWeek(),
                        Collectors.counting()
                ));

        return String.format("""
                유저 데이터:
                - 요일별 체크인 횟수: %s
                - 분석 기간: 최근 4주
                
                위 데이터를 바탕으로 유저의 습관 패턴을 분석하고
                어떤 요일/시간대에 습관을 잘 지키거나 놓치는지 인사이트를 제공해주세요.
                """,
                completedByDay.toString()
        );
    }
}