package org.embed.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AiReviewSummaryDTO {

	private Long id;
	private Long restaurantId;
	private String summaryText;
	private LocalDateTime updatedAt;

}