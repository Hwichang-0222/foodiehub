package org.embed.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.embed.dto.AiReviewSummaryDTO;
import org.embed.dto.ReviewDTO;
import org.embed.mapper.AiReviewSummaryMapper;
import org.embed.service.AiReviewSummaryService;
import org.embed.service.ClovaApiService;
import org.embed.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiReviewSummaryServiceImpl implements AiReviewSummaryService {

    private final AiReviewSummaryMapper aiReviewSummaryMapper;
    private final ReviewService reviewService;
    private final ClovaApiService clovaApiService;

    /**
     * 식당 ID로 AI 리뷰 요약 조회
     */
    @Override
    public AiReviewSummaryDTO findByRestaurantId(Long restaurantId) {
        log.debug("식당 ID {}의 AI 리뷰 요약 조회", restaurantId);
        return aiReviewSummaryMapper.findByRestaurantId(restaurantId);
    }

    /**
     * AI 리뷰 요약 저장 또는 업데이트
     * (이미 존재하면 UPDATE, 없으면 INSERT)
     */
    @Override
    @Transactional
    public int saveOrUpdateSummary(AiReviewSummaryDTO summary) {
        log.debug("AI 리뷰 요약 저장/업데이트: restaurantId={}", summary.getRestaurantId());

        // 기존 요약이 있는지 확인
        AiReviewSummaryDTO existing = aiReviewSummaryMapper.findByRestaurantId(summary.getRestaurantId());

        if (existing != null) {
            // 이미 존재하면 UPDATE
            log.debug("기존 AI 리뷰 요약 업데이트");
            return aiReviewSummaryMapper.updateSummary(summary);
        } else {
            // 없으면 INSERT
            log.debug("새 AI 리뷰 요약 저장");
            return aiReviewSummaryMapper.insertSummary(summary);
        }
    }

    /**
     * AI 리뷰 요약 삭제
     */
    @Override
    @Transactional
    public int deleteSummary(Long restaurantId) {
        log.debug("식당 ID {}의 AI 리뷰 요약 삭제", restaurantId);
        return aiReviewSummaryMapper.deleteSummary(restaurantId);
    }

    /**
     * 리뷰를 AI로 요약하여 저장
     */
    @Override
    @Transactional
    public AiReviewSummaryDTO generateAndSaveSummary(Long restaurantId) {
        log.info("===== AI 리뷰 요약 생성 시작: restaurantId={} =====", restaurantId);

        // 1. 해당 식당의 모든 리뷰 조회
        List<ReviewDTO> reviews = reviewService.findByRestaurantId(restaurantId);

        if (reviews == null || reviews.isEmpty()) {
            log.warn("리뷰가 없어 AI 요약을 생성할 수 없습니다.");
            return null;
        }

        log.info("조회된 리뷰 개수: {}", reviews.size());

        // 2. 리뷰 내용들을 하나의 문자열로 결합
        String allReviewContents = reviews.stream()
                .map(ReviewDTO::getContent)
                .collect(Collectors.joining("\n\n"));

        log.debug("결합된 리뷰 내용 길이: {} 글자", allReviewContents.length());

        // 3. CLOVA API를 통해 리뷰 요약 생성
        String summaryText;
        try {
            summaryText = clovaApiService.summarizeReviews(allReviewContents);
            log.info("AI 요약 생성 완료: {}", summaryText);
        } catch (Exception e) {
            log.error("CLOVA API 호출 실패", e);
            summaryText = "리뷰 요약 생성에 실패했습니다.";
        }

        // 4. DB에 저장 또는 업데이트
        AiReviewSummaryDTO summary = new AiReviewSummaryDTO();
        summary.setRestaurantId(restaurantId);
        summary.setSummaryText(summaryText);

        saveOrUpdateSummary(summary);
        log.info("AI 요약 DB 저장 완료");

        // 5. 저장된 요약 다시 조회하여 반환 (updated_at 포함)
        return aiReviewSummaryMapper.findByRestaurantId(restaurantId);
    }

}
