package org.embed.service.impl;

import java.util.List;

import org.embed.dto.AiReviewSummaryDTO;
import org.embed.dto.ReviewDTO;
import org.embed.mapper.AiReviewSummaryMapper;
import org.embed.service.AiReviewSummaryService;
import org.embed.service.ClovaApiService;
import org.embed.service.ReviewService;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiReviewSummaryServiceImpl implements AiReviewSummaryService {

    private final AiReviewSummaryMapper aiReviewSummaryMapper;
    private final ClovaApiService clovaApiService;
    private final ReviewService reviewService;

    @Override
    public AiReviewSummaryDTO findByRestaurantId(Long restaurantId) {
        return aiReviewSummaryMapper.findByRestaurantId(restaurantId);
    }

    @Override
    public int saveOrUpdateSummary(AiReviewSummaryDTO summary) {
        AiReviewSummaryDTO existing = aiReviewSummaryMapper.findByRestaurantId(summary.getRestaurantId());

        if (existing != null) {
            return aiReviewSummaryMapper.updateSummary(summary);
        } else {
            return aiReviewSummaryMapper.insertSummary(summary);
        }
    }

    @Override
    public int deleteSummary(Long restaurantId) {
        return aiReviewSummaryMapper.deleteSummary(restaurantId);
    }


    // ================================================
    // AI 요약 생성 & 저장 (메인 메서드)
    // ================================================
    @Override
    public AiReviewSummaryDTO generateAndSaveSummary(Long restaurantId) {

        // 1. 리뷰 가져오기
        List<ReviewDTO> reviews = reviewService.findByRestaurantId(restaurantId);
        if (reviews.isEmpty()) {
            log.warn("리뷰 없음 → AI 요약 불가");
            return null;
        }

        // 2. 프롬프트 생성
        StringBuilder prompt = new StringBuilder();
        prompt.append("너는 한국 음식점 리뷰를 매우 간단하게 요약하는 전문가다.\n");
        prompt.append("아래 리뷰들을 분석해 긍정 / 부정 / 전체 요약을 만들어라.\n");
        prompt.append("각 항목은 반드시 1~2문장 이내로만 작성한다.\n");
        prompt.append("리뷰 문장을 그대로 복사하지 말고 공통된 핵심만 압축해서 정리한다.\n");
        prompt.append("JSON 외의 텍스트는 절대 포함하지 마라.\n");
        prompt.append("코드블록 없이 순수 JSON만 반환하라.\n\n");

        prompt.append("{\n");
        prompt.append("  \"positive\": \"\",\n");
        prompt.append("  \"negative\": \"\",\n");
        prompt.append("  \"summary\": \"\"\n");
        prompt.append("}\n\n");

        prompt.append("리뷰 목록:\n");
        for (ReviewDTO r : reviews) {
            prompt.append("- ").append(r.getContent()).append("\n");
        }

        // 3. Clova 호출
        String aiResponse = clovaApiService.generateSummary(prompt.toString());
        log.debug("AI 응답 원본: {}", aiResponse);

        // 4. JSON 부분만 정제
        String cleanJson = extractContentJson(aiResponse);
        log.debug("정제된 content(JSON 부분만): {}", cleanJson);

        if (cleanJson == null) {
            log.error("content JSON 정제 실패");
            return null;
        }

        // 5. JSONObject 변환
        JSONObject json = new JSONObject(cleanJson);

        // 6. summaryKey 우선 순위 처리
        String summaryText = extractSummaryText(json);

        // 7. DB 저장
        AiReviewSummaryDTO existing = aiReviewSummaryMapper.findByRestaurantId(restaurantId);

        if (existing == null) {
            AiReviewSummaryDTO dto = new AiReviewSummaryDTO();
            dto.setRestaurantId(restaurantId);
            dto.setSummaryText(summaryText);
            aiReviewSummaryMapper.insertSummary(dto);
            return dto;
        } else {
            existing.setSummaryText(summaryText);
            aiReviewSummaryMapper.updateSummary(existing);
            return existing;
        }
    }


    // ================================================
    // 내용 파싱 유틸
    // ================================================

    // Clova 응답의 내부 content(JSON)만 꺼냄
    private String extractContentJson(String aiResponse) {
        try {
            JSONObject root = new JSONObject(aiResponse);
            JSONObject result = root.getJSONObject("result");
            JSONObject message = result.getJSONObject("message");

            String content = message.getString("content");

            // ```json, ``` 같은 코드블록 제거
            content = content.replace("```json", "")
                             .replace("```", "")
                             .trim();

            return content;
        }
        catch (Exception e) {
            log.error("extractContentJson 실패", e);
            return null;
        }
    }

    // JSON에서 summary 필드 우선 추출, 없으면 직접 조합
    private String extractSummaryText(JSONObject json) {
        // 1) API가 summary 를 준 경우
        if (json.has("summary")) {
            return json.optString("summary", "");
        }

        // 2) API가 "긍정", "부정"을 배열로 줄 때 (지금 네 응답이 이 방식)
        StringBuilder sb = new StringBuilder();

        if (json.has("긍정")) {
            sb.append("긍정 리뷰: ");
            sb.append(json.getJSONArray("긍정").join(", "));
            sb.append(". ");
        }

        if (json.has("부정")) {
            sb.append("부정 리뷰: ");
            sb.append(json.getJSONArray("부정").join(", "));
        }

        return sb.toString();
    }
}
