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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

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
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

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

        return mapper.toDto(profileRepo.save(profile));
    }

    @Override
    public TranslatorProfileDto getById(Long id) {
        return profileRepo.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
    }

    @Override
    public TranslatorProfileDto update(Long id, TranslatorProfileDto dto) {
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

        return mapper.toDto(profileRepo.save(profile));
    }

    @Override
    public void delete(Long id) {
        TranslatorProfile profile = profileRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        profileRepo.delete(profile);
    }
}

