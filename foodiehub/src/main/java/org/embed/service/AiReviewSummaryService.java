package org.embed.service;

import org.embed.dto.AiReviewSummaryDTO;

public interface AiReviewSummaryService {
	
	// AI 리뷰 요약 조회
	AiReviewSummaryDTO findByRestaurantId(Long restaurantId);
	
	// AI 리뷰 요약 등록 또는 수정
	int saveOrUpdateSummary(AiReviewSummaryDTO summary);
	
	// AI 리뷰 요약 삭제
	int deleteSummary(Long restaurantId);
	
	AiReviewSummaryDTO generateAndSaveSummary(Long restaurantId);

}