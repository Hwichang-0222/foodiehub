package org.embed.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class ReviewDTO {
	
    private Long id;
    private Long restaurantId;     // 리뷰 대상 식당
    private Long userId;           // 작성자
    private Long parentId;         // 부모 리뷰 (댓글/대댓글)
    private String content;        // 내용
    private Integer rating;        // 별점 (댓글이면 NULL)
    private Boolean isReply;       // 댓글 여부
    private LocalDateTime createdAt;

    // JOIN 및 프론트 표시용 필드
    private String userName;       // 유저 이름
    private String profileImageUrl; // 유저 프로필 이미지
    private String restaurantName; // 식당 이름

    // 리뷰에 포함된 이미지들 (1:N)
    private List<ImageDTO> images;

    // 댓글 / 대댓글 리스트 (계층 구조)
    private List<ReviewDTO> replies;
	
}
