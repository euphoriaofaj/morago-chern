package com.morago.backend.service;

import com.morago.backend.dto.ThemeDto;

import java.util.List;

public interface ThemeService {
    ThemeDto create(ThemeDto dto);
    ThemeDto update(Long id, ThemeDto dto);
    void delete(Long id);
    ThemeDto getById(Long id);
    List<ThemeDto> getAll();
}
