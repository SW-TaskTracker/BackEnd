package org.swengineer.ai.dto.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data

public class ChatGptRequest {
    private String model;
    private List<Message> messages=new ArrayList<>();


    public ChatGptRequest(String model) {
        this.model = model;
    }

    public void addSystemMessage(String content) {
        this.messages.add(new Message("system", content));
    }


}
