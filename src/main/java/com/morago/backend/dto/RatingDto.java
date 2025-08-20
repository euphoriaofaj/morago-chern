package com.morago.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingDto {
    private Long id;
    private Long userId;
    private Long translatorProfileId;
    private int score;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
