package org.embed.service.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.embed.service.ClovaApiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ClovaApiServiceImpl implements ClovaApiService {

    @Value("${clova.api.key}")
    private String apiKey;

    @Value("${clova.api.endpoint}")
    private String endpoint;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String generateSummary(String prompt) {

        // 헤더 설정: SSE 제거하고 일반 JSON으로 받기
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("X-NCP-CLOVASTUDIO-REQUEST-ID", UUID.randomUUID().toString());
        headers.set("Accept", "application/json");

        // 스트리밍 비활성화 (JSON 한 덩어리로 받기)
        Map<String, Object> requestBody = Map.of(
            "messages", List.of(
                Map.of("role", "system", "content", List.of(
                        Map.of("type", "text", "text", "")
                )),
                Map.of("role", "user", "content", List.of(
                        Map.of("type", "text", "text", prompt)
                ))
            ),
            "topP", 0.8,
            "topK", 0,
            "maxTokens", 3000,
            "temperature", 0.5,
            "repetitionPenalty", 1.1,
            "stop", List.of(),
            "seed", 0,
            "includeAiFilters", true,
            "isStreaming", false
        );

        HttpEntity<Object> httpEntity = new HttpEntity<>(requestBody, headers);

        return restTemplate.postForObject(endpoint, httpEntity, String.class);
    }
}
