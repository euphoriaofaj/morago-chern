package com.morago.backend.service;

import com.morago.backend.dto.LanguageDto;

import java.util.List;

public interface LanguageService {
    LanguageDto create(LanguageDto dto);
    LanguageDto update(Long id, LanguageDto dto);
    void delete(Long id);
    LanguageDto getById(Long id);
    List<LanguageDto> getAll();
}
