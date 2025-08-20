package com.morago.backend.service;

import com.morago.backend.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(CategoryDto categoryDto);
    CategoryDto update(Long id, CategoryDto categoryDto);
    void delete(Long id);
    CategoryDto getById(Long id);
    List<CategoryDto> getAll();
}
