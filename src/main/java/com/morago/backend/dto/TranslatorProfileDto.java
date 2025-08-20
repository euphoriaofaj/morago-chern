package com.morago.backend.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class TranslatorProfileDto {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDate dateOfBirth;
    private String email;
    private Boolean isAvailable;
    private Boolean isOnline;
    private String levelOfKorean;
    private LocalDateTime updatedAt;

    private Long userId;
    private Set<Long> languageIds;
    private Set<Long> themeIds;
}
