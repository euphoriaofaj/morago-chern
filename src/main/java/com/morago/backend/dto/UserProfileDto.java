package com.morago.backend.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDto {
    private Long id;
    private Long userId;
    private boolean isFreeCallMade;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

