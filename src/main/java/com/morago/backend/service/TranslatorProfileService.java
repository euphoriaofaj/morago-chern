package com.morago.backend.service;

import com.morago.backend.dto.TranslatorProfileDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TranslatorProfileService {

    TranslatorProfileDto create(TranslatorProfileDto dto);

    TranslatorProfileDto getById(Long id);

    Page<TranslatorProfileDto> getAllWithFilters(
            Pageable pageable,
            Boolean isAvailable,
            Boolean isOnline,
            Long languageId,
            Long themeId,
            String levelOfKorean
    );

    Page<TranslatorProfileDto> getAvailableByTheme(Long themeId, Pageable pageable);

    TranslatorProfileDto update(Long id, TranslatorProfileDto dto);

    TranslatorProfileDto updateAvailability(Long id, Boolean isAvailable);

    TranslatorProfileDto updateOnlineStatus(Long id, Boolean isOnline);

    boolean isProfileOwner(Long profileId, String username);

    void delete(Long id);
}
