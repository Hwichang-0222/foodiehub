package org.embed.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class RestaurantDTO {
	
	private Long id;
	private Long ownerId;
	private String name;
	private String description;
	private String address;
	private String regionLevel1;
	private String regionLevel2;
	private String regionLevel3;
	private String category;
	private Double latitude;
	private Double longitude;
	private String mainImageUrl;
	private LocalDateTime createdAt;
	private Double avgRating;			//별점 평균 계산용
	private Integer reviewCount;		//리뷰갯수 카운팅
	private String ownerName;
	
}
