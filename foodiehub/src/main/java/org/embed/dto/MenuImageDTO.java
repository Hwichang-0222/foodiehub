package org.embed.dto;

import lombok.Data;

@Data
public class MenuImageDTO {
    private Long id;
    private Long restaurantId;  // FK 변경됨
    private String imageUrl;
    private String createdAt;
}
