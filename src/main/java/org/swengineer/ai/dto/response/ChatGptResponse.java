package org.swengineer.ai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.swengineer.ai.dto.request.Message;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ChatGptResponse {
    private List<Choice> choices;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice {
        private int index;
        private Message message;
    }
}
