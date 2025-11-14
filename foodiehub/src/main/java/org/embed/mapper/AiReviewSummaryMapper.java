package org.embed.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.embed.dto.AiReviewSummaryDTO;

@Mapper
public interface AiReviewSummaryMapper {
	
	// AI 리뷰 요약 조회
	AiReviewSummaryDTO findByRestaurantId(@Param("restaurantId") Long restaurantId);
	
	// AI 리뷰 요약 등록
    int insertSummary(AiReviewSummaryDTO summary);
    
	// AI 리뷰 요약 수정
	int updateSummary(AiReviewSummaryDTO summary);
	
	// AI 리뷰 요약 삭제
	int deleteSummary(@Param("restaurantId") Long restaurantId);

}