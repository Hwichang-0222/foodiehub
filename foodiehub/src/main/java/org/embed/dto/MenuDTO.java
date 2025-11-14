package org.embed.dto;

import lombok.Data;

@Data
public class MenuDTO {
    private Long id;
    private Long restaurantId;
    private String name;
    private Integer price;
    private String description;
    private String createdAt;
}
