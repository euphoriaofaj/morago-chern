package com.morago.backend.service;

import com.morago.backend.dto.TranslatorProfileDto;

public interface TranslatorProfileService {

    TranslatorProfileDto create(TranslatorProfileDto dto);

    TranslatorProfileDto getById(Long id);

    TranslatorProfileDto update(Long id, TranslatorProfileDto dto);

    void delete(Long id);
}
