package com.morago.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThemeDto {

    private Long id;

    private String name;

    private String koreanTitle;

    private BigDecimal price;

    private BigDecimal nightPrice;

    private String description;

    private boolean isPopular;

    private boolean isActive;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long categoryId;
    private Long iconFileId;

    private List<Long> translatorProfileIds;

    private List<TranslatorProfileDto> translators;
}
