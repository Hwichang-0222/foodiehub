package org.embed.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.embed.service.ClovaApiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClovaApiServiceImpl implements ClovaApiService {

    @Value("${naver.clova.api.key}")
    private String apiKey;

    @Value("${naver.clova.studio.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String summarizeReviews(String reviewContents) {
        log.debug("클로바 RAG Reasoning API 호출 시작");

        try {
            // 요청 헤더 설정 (curl 예제 형식)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("X-NCP-CLOVASTUDIO-REQUEST-ID", UUID.randomUUID().toString().replace("-", ""));

            // 요청 바디 생성
            Map<String, Object> requestBody = new HashMap<>();

            // messages 배열 생성
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", "다음 리뷰들을 3-5문장으로 요약해주세요. 긍정적인 점과 부정적인 점을 포함해주세요:\n\n" + reviewContents);

            requestBody.put("messages", List.of(userMessage));

            // curl 예제와 동일한 파라미터
            requestBody.put("topP", 0.8);
            requestBody.put("topK", 0);
            requestBody.put("maxTokens", 1024);
            requestBody.put("temperature", 0.5);
            requestBody.put("repetitionPenalty", 1.1);
            requestBody.put("stop", List.of());
            requestBody.put("seed", 0);
            requestBody.put("includeAiFilters", true);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.debug("CLOVA API 요청: URL={}", apiUrl);

            // API 호출
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

            log.debug("CLOVA API 응답 상태: {}", response.getStatusCode());

            // 응답 파싱
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("result")) {
                Map<String, Object> result = (Map<String, Object>) responseBody.get("result");
                if (result.containsKey("message")) {
                    Map<String, Object> message = (Map<String, Object>) result.get("message");
                    String summary = (String) message.get("content");
                    log.info("AI 요약 생성 성공");
                    return summary;
                }
            }

            log.warn("예상치 못한 응답 형식: {}", responseBody);
            return "리뷰 요약을 생성했지만 형식을 파싱할 수 없습니다.";

        } catch (Exception e) {
            log.error("클로바 스튜디오 API 호출 실패", e);
            throw e;
        }
    }
}
