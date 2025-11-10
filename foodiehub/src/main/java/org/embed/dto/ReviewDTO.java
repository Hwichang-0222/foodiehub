package org.embed.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class ReviewDTO {

	private Long id;
	private Long restaurantId;
	private Long userId;
	private Long parentId;
	private String content;
	private Integer rating;
	private Boolean isReply;
	private LocalDateTime createdAt;

	private String userName;
	private String profileImageUrl;
	private String restaurantName;

	private List<ImageDTO> images;

	private List<ReviewDTO> replies;

}
