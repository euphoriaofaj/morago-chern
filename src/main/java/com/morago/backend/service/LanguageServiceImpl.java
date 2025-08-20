package com.morago.backend.service;

import com.morago.backend.dto.LanguageDto;
import com.morago.backend.entity.Language;
import com.morago.backend.entity.TranslatorProfile;
import com.morago.backend.exception.ResourceNotFoundException;
import com.morago.backend.mapper.LanguageMapper;
import com.morago.backend.repository.LanguageRepository;
import com.morago.backend.repository.TranslatorProfileRepository;
import com.morago.backend.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository languageRepository;
    private final TranslatorProfileRepository translatorProfileRepository;
    private final LanguageMapper languageMapper;

    private Language getLanguageOrThrow(Long id) {
        return languageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Language not found with id " + id));
    }

    @Override
    public LanguageDto create(LanguageDto dto) {
        Language language = languageMapper.toEntity(dto);

        if (dto.getTranslatorProfileIds() != null) {
            List<TranslatorProfile> profiles = translatorProfileRepository.findAllById(dto.getTranslatorProfileIds());
            language.setTranslatorProfiles(new java.util.HashSet<>(profiles));
        }

        return languageMapper.toDto(languageRepository.save(language));
    }

    @Override
    public LanguageDto update(Long id, LanguageDto dto) {
        Language existing = getLanguageOrThrow(id);

        existing.setName(dto.getName());

        if (dto.getTranslatorProfileIds() != null) {
            List<TranslatorProfile> profiles = translatorProfileRepository.findAllById(dto.getTranslatorProfileIds());
            existing.setTranslatorProfiles(new java.util.HashSet<>(profiles));
        }

        return languageMapper.toDto(languageRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        Language language = getLanguageOrThrow(id);
        languageRepository.delete(language);
    }

    @Override
    public LanguageDto getById(Long id) {
        return languageMapper.toDto(getLanguageOrThrow(id));
    }

    @Override
    public List<LanguageDto> getAll() {
        return languageRepository.findAll()
                .stream()
                .map(languageMapper::toDto)
                .toList();
    }
}
