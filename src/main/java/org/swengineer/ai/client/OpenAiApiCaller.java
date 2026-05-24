//package org.swengineer.ai.client;
//
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.*;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.List;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
////호출 컴포넌트
//public class OpenAiApiCaller {
//
//    private final RestTemplate restTemplate;
//
//    @Value("${openai.model}")
//    private String model;
//
//
//    @Value("${openai.api.url}")
//    private String apiURL;
//
//    public ChatGptResponse call(ChatGptRequest request) {
//
//        try {
//            ChatGptResponse response =
//                    restTemplate.postForObject(apiURL, request, ChatGptResponse.class);
//
//            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()
//                    || response.getChoices().get(0).getMessage() == null) {
//                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "OpenAI 응답이 비어 있습니다.");
//            }
//
//            return response;
//
//        } catch (HttpClientErrorException e) { // 4xx
//            if (e.getStatusCode().value() == 429) {
//                log.warn("OpenAI rate limit exceeded: {}", safeBody(e));
//                throw new ResponseStatusException(
//                        HttpStatus.SERVICE_UNAVAILABLE,
//                        "OpenAI 요청 한도 초과했습니다. 잠시 후 다시 시도해 주세요."
//                );
//            }
//            log.error("OpenAI client error ({}): {}", e.getStatusCode(), safeBody(e));
//            throw new ResponseStatusException(
//
//                    HttpStatus.BAD_GATEWAY,
//                    "OpenAI 요청 처리 중 오류가 발생했습니다."
//
//            );
//
//        }
//        catch (HttpServerErrorException e) {
//            log.error("OpenAI server error ({}): {}", e.getStatusCode(), safeBody(e));
//            // 5xx
//            throw new ResponseStatusException(
//                    HttpStatus.BAD_GATEWAY,
//                    "OpenAI 서버에 오류가 발생했습니다."
//            );
//
//        } catch (ResourceAccessException e) { // timeout/connection
//            throw new ResponseStatusException(
//                    HttpStatus.GATEWAY_TIMEOUT,
//                    "OpenAI 연결 실패 또는 타임아웃"
//            );
//
//        } catch (RestClientException e) { // 기타
//            throw new ResponseStatusException(
//                    HttpStatus.INTERNAL_SERVER_ERROR,
//                    "OpenAI 호출 중 오류 발생"
//            );
//        }
//    }
//
//    private String safeBody(RestClientResponseException e) {
//        String body = e.getResponseBodyAsString();
//        return (body == null || body.isBlank()) ? "(no body)" : body;
//    }
//
//
//
//
//    public String callMessages(List<Message> messages) {
//        ChatGptRequest request = new ChatGptRequest(model);
//        request.setMessages(messages);
//
//        ChatGptResponse response = call(request);
//        return response.getChoices()
//                .get(0)
//                .getMessage()
//                .getContent();
//    }
//}
