package org.embed.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.embed.dto.AiReviewSummaryDTO;

@Mapper
public interface AiReviewSummaryMapper {

    /**
     * 식당 ID로 AI 리뷰 요약 조회
     * @param restaurantId 식당 ID
     * @return AI 리뷰 요약 (없으면 null)
     */
    AiReviewSummaryDTO findByRestaurantId(Long restaurantId);

    /**
     * AI 리뷰 요약 저장 (INSERT)
     * @param summary AI 리뷰 요약 DTO
     * @return 저장 성공 시 1
     */
    int insertSummary(AiReviewSummaryDTO summary);

    /**
     * AI 리뷰 요약 수정 (UPDATE)
     * @param summary AI 리뷰 요약 DTO
     * @return 수정 성공 시 1
     */
    int updateSummary(AiReviewSummaryDTO summary);

    /**
     * AI 리뷰 요약 삭제 (DELETE)
     * @param restaurantId 식당 ID
     * @return 삭제 성공 시 1
     */
    int deleteSummary(Long restaurantId);

}
