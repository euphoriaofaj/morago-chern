package com.morago.backend.service;

import com.morago.backend.dto.request.TranslatorProfileCreateRequest;
import com.morago.backend.dto.request.TranslatorProfileUpdateRequest;
import com.morago.backend.dto.response.TranslatorProfileResponse;
import com.morago.backend.dto.response.TranslatorProfileSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TranslatorProfileService {

    TranslatorProfileResponse create(TranslatorProfileCreateRequest request);

    TranslatorProfileResponse getById(Long id);
    
    TranslatorProfileResponse getByUserId(Long userId);

    Page<TranslatorProfileSummaryResponse> getAllWithFilters(
            Pageable pageable,
            Boolean isAvailable,
            Boolean isOnline,
            Long languageId,
            Long themeId,
            String levelOfKorean,
            String search
    );

    Page<TranslatorProfileSummaryResponse> getAvailableByTheme(Long themeId, Pageable pageable);

    TranslatorProfileResponse update(Long id, TranslatorProfileUpdateRequest request);

    TranslatorProfileResponse updateAvailability(Long id, Boolean isAvailable);

    TranslatorProfileResponse updateOnlineStatus(Long id, Boolean isOnline);

    boolean isProfileOwner(Long profileId, String username);
    
    boolean canAccessProfile(Long profileId, String username, String role);

    void delete(Long id);
    
    // Statistics methods
    Long getTotalTranslators();
    Long getOnlineTranslators();
    Long getAvailableTranslators();
}
