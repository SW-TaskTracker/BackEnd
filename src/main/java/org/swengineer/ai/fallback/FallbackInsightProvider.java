// FallbackInsightProvider.java
package org.swengineer.ai.fallback;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Random;

@Component
public class FallbackInsightProvider {
    // REQ-09 (AI 인사이트) 에러 시 기본 메시지
    private static final List<String> FALLBACK_INSIGHTS = List.of(
            "최근 꾸준히 습관을 실천하고 있어요! 이 페이스를 유지해보세요 💪",
            "습관을 기록하는 것 자체가 성장의 시작이에요. 계속 이어나가 봐요 🌱",
            "데이터를 분석 중이에요. 꾸준히 체크인하면 더 정확한 패턴을 알려드릴게요 📊",
            "작은 습관들이 쌓여 큰 변화를 만들어요. 오늘도 잘 하고 있어요 ✨",
            "습관 유지율이 높아지고 있어요! 조금만 더 힘내봐요 🔥"
    );

    private final Random random = new Random();

    public String getRandom() {
        return FALLBACK_INSIGHTS.get(random.nextInt(FALLBACK_INSIGHTS.size()));
    }
}