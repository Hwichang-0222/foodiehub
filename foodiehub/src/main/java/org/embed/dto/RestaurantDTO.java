package org.embed.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class RestaurantDTO {
	
	private Long id;
	private String name;
	private String description;
	private String address;
	private String region;
	private String category;
	private Double latitude;
	private Double longitude;
	private String mainImageUrl;
	private LocalDateTime createdAt;
	
}
