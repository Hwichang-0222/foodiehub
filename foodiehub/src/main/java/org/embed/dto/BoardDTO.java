package org.embed.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BoardDTO {
    private Long id;
    private Long userId;
    private Long parentId;        // 관리자 답변용
    private String title;
    private String content;
    private String category;      // NOTICE, GENERAL, QUESTION, SUGGESTION
    private Boolean isPrivate;    // 문의/건의 비공개 여부
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int viewCount;
    // join용
    private String userName;
    private int displayNumber;
}
