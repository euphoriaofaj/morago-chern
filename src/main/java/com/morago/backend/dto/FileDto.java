package com.morago.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FileDto {
    private Long id;
    private String originalTitle;
    private String path;
    private String type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long userId;
}
