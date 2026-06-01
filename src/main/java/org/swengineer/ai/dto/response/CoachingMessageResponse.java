package org.swengineer.ai.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)  // null 필드는 JSON에서 제외
public record CoachingMessageResponse(
        boolean hasMessage,
        String content,
        LocalDateTime generatedAt,
        String message  // 메시지 없을 때 안내 문구
) {
    // 메시지 있을 때
    public static CoachingMessageResponse of(String content, LocalDateTime generatedAt) {
        return new CoachingMessageResponse(true, content, generatedAt, null);
    }

    // 메시지 없을 때
    public static CoachingMessageResponse empty() {
        return new CoachingMessageResponse(
                false, null, null,
                "아직 코칭 메시지가 없어요! 내일 아침 7시에 첫 메시지가 도착할 거예요 🌅"
        );
    }
}