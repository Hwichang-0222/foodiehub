package org.embed.service;

import org.embed.dto.AiReviewSummaryDTO;

public interface AiReviewSummaryService {

    /**
     * 식당 ID로 AI 리뷰 요약 조회
     * @param restaurantId 식당 ID
     * @return AI 리뷰 요약 (없으면 null)
     */
    AiReviewSummaryDTO findByRestaurantId(Long restaurantId);

    /**
     * AI 리뷰 요약 저장 또는 업데이트
     * (이미 존재하면 UPDATE, 없으면 INSERT)
     * @param summary AI 리뷰 요약 DTO
     * @return 성공 여부
     */
    int saveOrUpdateSummary(AiReviewSummaryDTO summary);

    /**
     * AI 리뷰 요약 삭제
     * @param restaurantId 식당 ID
     * @return 성공 여부
     */
    int deleteSummary(Long restaurantId);

    /**
     * 리뷰를 AI로 요약하여 저장
     * @param restaurantId 식당 ID
     * @return 생성된 AI 리뷰 요약 (리뷰가 없으면 null)
     */
    AiReviewSummaryDTO generateAndSaveSummary(Long restaurantId);

}
