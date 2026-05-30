package org.swengineer.ai.fallback;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class FallbackMessageProvider {
    //보안 및 에러핸들링 ( 기본 고정 메시지 하드 코딩 )
    //REQ-10 (AI 코칭)
    private static final List<String> FALLBACK_MESSAGES = List.of(
            "오늘도 한 걸음씩! 🌱\n작은 습관이 큰 변화를 만듭니다.\n오늘의 루틴을 시작해볼까요?",
            "어제보다 나은 오늘을 만들어봐요 💪\n꾸준함이 가장 강한 힘입니다.\n오늘 목표를 확인해보세요!",
            "좋은 아침이에요! ☀️\n습관은 매일 쌓이는 복리입니다.\n오늘도 함께 해봐요.",
            "포기하지 않는 것 자체가 성공이에요 🔥\n오늘 하루도 당신을 응원합니다.\n지금 바로 시작해볼까요?",
            "매일 조금씩, 반드시 달라집니다 ✨\n오늘의 작은 실천이 미래를 바꿔요.\n오늘 습관을 확인해보세요!",
            "잠깐 멈춰도 괜찮아요 🌿\n다시 시작하는 용기가 더 중요합니다.\n오늘도 할 수 있어요.",
            "스트릭은 숫자가 아니라 태도예요 💡\n꾸준히 도전하는 당신이 멋집니다.\n오늘도 화이팅!",
            "오늘을 잘 보내면 내일이 달라져요 🎯\n습관 체크, 지금 시작해봐요.\n당신의 루틴을 응원합니다."
    );

    private final Random random = new Random();

    public String getRandom() {
        return FALLBACK_MESSAGES.get(random.nextInt(FALLBACK_MESSAGES.size()));
    }


}
