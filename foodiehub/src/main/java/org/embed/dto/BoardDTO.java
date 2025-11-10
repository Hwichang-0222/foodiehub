package org.embed.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BoardDTO {
	private Long id;
	private Long userId;
	private Long parentId;
	private String title;
	private String content;
	private String category;
	private Boolean isPrivate;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private int viewCount;
	private String userName;
}
