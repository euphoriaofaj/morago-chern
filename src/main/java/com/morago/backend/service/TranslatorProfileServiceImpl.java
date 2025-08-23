package com.morago.backend.service;

import com.morago.backend.dto.TranslatorProfileDto;
import com.morago.backend.entity.Language;
import com.morago.backend.entity.Theme;
import com.morago.backend.entity.TranslatorProfile;
import com.morago.backend.entity.User;
import com.morago.backend.exception.ResourceNotFoundException;
import com.morago.backend.mapper.TranslatorProfileMapper;
import com.morago.backend.repository.LanguageRepository;
import com.morago.backend.repository.ThemeRepository;
import com.morago.backend.repository.TranslatorProfileRepository;
import com.morago.backend.repository.UserRepository;
import com.morago.backend.service.TranslatorProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TranslatorProfileServiceImpl implements TranslatorProfileService {

    private final TranslatorProfileRepository profileRepo;
    private final UserRepository userRepo;
    private final LanguageRepository languageRepo;
    private final ThemeRepository themeRepo;
    private final TranslatorProfileMapper mapper;

    @Override
    public TranslatorProfileDto create(TranslatorProfileDto dto) {
        log.debug("Creating translator profile for user ID: {}", dto.getUserId());
        
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if profile already exists for this user
        if (profileRepo.existsByUserId(dto.getUserId())) {
            throw new IllegalArgumentException("Translator profile already exists for this user");
        }

        Set<Language> languages = (dto.getLanguageIds() != null)
                ? dto.getLanguageIds().stream()
                .map(id -> languageRepo.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Language not found: " + id)))
                .collect(Collectors.toSet())
                : Set.of();

        Set<Theme> themes = (dto.getThemeIds() != null)
                ? dto.getThemeIds().stream()
                .map(id -> themeRepo.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Theme not found: " + id)))
                .collect(Collectors.toSet())
                : Set.of();

        TranslatorProfile profile = TranslatorProfile.builder()
                .dateOfBirth(dto.getDateOfBirth())
                .email(dto.getEmail())
                .isAvailable(dto.getIsAvailable())
                .isOnline(dto.getIsOnline())
                .levelOfKorean(dto.getLevelOfKorean())
                .user(user)
                .languages(languages)
                .themes(themes)
                .build();

        TranslatorProfile saved = profileRepo.save(profile);
        log.info("Translator profile created successfully with ID: {}", saved.getId());
        return mapper.toDto(saved);
    }

    @Override
    public TranslatorProfileDto getById(Long id) {
        return profileRepo.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    @Override
    public Page<TranslatorProfileDto> getAllWithFilters(
            Pageable pageable,
            Boolean isAvailable,
            Boolean isOnline,
            Long languageId,
            Long themeId,
            String levelOfKorean) {
        
        log.debug("Fetching translator profiles with filters - available: {}, online: {}, language: {}, theme: {}", 
                isAvailable, isOnline, languageId, themeId);
        
        Page<TranslatorProfile> profiles = profileRepo.findWithFilters(
                isAvailable, isOnline, languageId, themeId, levelOfKorean, pageable);
        
        return profiles.map(mapper::toDto);
    }

    @Override
    public Page<TranslatorProfileDto> getAvailableByTheme(Long themeId, Pageable pageable) {
        log.debug("Fetching available translators for theme ID: {}", themeId);
        
        Page<TranslatorProfile> profiles = profileRepo.findAvailableByTheme(themeId, pageable);
        return profiles.map(mapper::toDto);
    }
    @Override
    public TranslatorProfileDto update(Long id, TranslatorProfileDto dto) {
        log.debug("Updating translator profile with ID: {}", id);
        
        TranslatorProfile profile = profileRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        if (dto.getDateOfBirth() != null) profile.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getEmail() != null) profile.setEmail(dto.getEmail());
        if (dto.getIsAvailable() != null) profile.setIsAvailable(dto.getIsAvailable());
        if (dto.getIsOnline() != null) profile.setIsOnline(dto.getIsOnline());
        if (dto.getLevelOfKorean() != null) profile.setLevelOfKorean(dto.getLevelOfKorean());

        if (dto.getLanguageIds() != null) {
            Set<Language> languages = dto.getLanguageIds().stream()
                    .map(idL -> languageRepo.findById(idL)
                            .orElseThrow(() -> new ResourceNotFoundException("Language not found: " + idL)))
                    .collect(Collectors.toSet());
            profile.setLanguages(languages);
        }

        if (dto.getThemeIds() != null) {
            Set<Theme> themes = dto.getThemeIds().stream()
                    .map(idT -> themeRepo.findById(idT)
                            .orElseThrow(() -> new ResourceNotFoundException("Theme not found: " + idT)))
                    .collect(Collectors.toSet());
            profile.setThemes(themes);
        }

        TranslatorProfile updated = profileRepo.save(profile);
        log.info("Translator profile updated successfully with ID: {}", updated.getId());
        return mapper.toDto(updated);
    }

    @Override
    public TranslatorProfileDto updateAvailability(Long id, Boolean isAvailable) {
        log.debug("Updating availability for translator profile ID: {} to {}", id, isAvailable);
        
        TranslatorProfile profile = profileRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        
        profile.setIsAvailable(isAvailable);
        TranslatorProfile updated = profileRepo.save(profile);
        
        log.info("Availability updated successfully for translator profile ID: {}", id);
        return mapper.toDto(updated);
    }

    @Override
    public TranslatorProfileDto updateOnlineStatus(Long id, Boolean isOnline) {
        log.debug("Updating online status for translator profile ID: {} to {}", id, isOnline);
        
        TranslatorProfile profile = profileRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        
        profile.setIsOnline(isOnline);
        TranslatorProfile updated = profileRepo.save(profile);
        
        log.info("Online status updated successfully for translator profile ID: {}", id);
        return mapper.toDto(updated);
    }

    @Override
    public boolean isProfileOwner(Long profileId, String username) {
        return profileRepo.findById(profileId)
                .map(profile -> profile.getUser().getUsername().equals(username))
                .orElse(false);
    }

    @Override
    public void delete(Long id) {
        log.debug("Deleting translator profile with ID: {}", id);
        
        TranslatorProfile profile = profileRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        profileRepo.delete(profile);
        
        log.info("Translator profile deleted successfully with ID: {}", id);
    }
}

