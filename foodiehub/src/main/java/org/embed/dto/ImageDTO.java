package org.embed.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ImageDTO {

    private Long id;
    private Long reviewId;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private LocalDateTime createdAt;
    
}