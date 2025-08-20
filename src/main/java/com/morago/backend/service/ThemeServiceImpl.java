package com.morago.backend.service;

import com.morago.backend.dto.ThemeDto;
import com.morago.backend.entity.Theme;
import com.morago.backend.exception.ResourceNotFoundException;
import com.morago.backend.mapper.ThemeMapper;
import com.morago.backend.repository.ThemeRepository;
import com.morago.backend.service.ThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ThemeServiceImpl implements ThemeService {

    private final ThemeRepository themeRepository;
    private final ThemeMapper themeMapper;


    private Theme getThemeOrThrow(Long id) {
        return themeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theme not found with id: " + id));
    }

    @Override
    public ThemeDto create(ThemeDto dto) {
        Theme theme = themeMapper.toEntity(dto);
        Theme saved = themeRepository.save(theme);
        return themeMapper.toDto(saved);
    }

    @Override
    public ThemeDto update(Long id, ThemeDto dto) {
        Theme existing = getThemeOrThrow(id);


        existing.setName(dto.getName());
        existing.setKoreanTitle(dto.getKoreanTitle());
        existing.setPrice(dto.getPrice());
        existing.setNightPrice(dto.getNightPrice());
        existing.setDescription(dto.getDescription());
        existing.setPopular(dto.isPopular());
        existing.setActive(dto.isActive());


//        if (dto.getCategoryId() != null) {
//            existing.setCategory(themeMapper.map(dto.getCategoryId()));
//        }
//        if (dto.getIconFileId() != null) {
//            existing.setIcon(themeMapper.mapFile(dto.getIconFileId()));
//        }
//        if (dto.getTranslatorProfileIds() != null) {
//            existing.setTranslators(themeMapper.mapTranslatorEntities(dto.getTranslatorProfileIds()));
//        }

        Theme updated = themeRepository.save(existing);
        return themeMapper.toDto(updated);
    }

    @Override
    public void delete(Long id) {
        Theme theme = getThemeOrThrow(id);
        themeRepository.delete(theme);
    }

    @Override
    public ThemeDto getById(Long id) {
        Theme theme = getThemeOrThrow(id);
        return themeMapper.toDto(theme);
    }

    @Override
    public List<ThemeDto> getAll() {
        return themeRepository.findAll().stream()
                .map(themeMapper::toDto)
                .toList();
    }
}
