package com.morago.backend.service;

import com.morago.backend.dto.CategoryDto;
import com.morago.backend.entity.Category;
import com.morago.backend.exception.ResourceNotFoundException;
import com.morago.backend.mapper.CategoryMapper;
import com.morago.backend.repository.CategoryRepository;
import com.morago.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    private Category getEntityOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id));
    }

    @Override
    public CategoryDto create(CategoryDto categoryDto) {
        Category category = categoryMapper.toEntity(categoryDto);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        Category existing = getEntityOrThrow(id);
        existing.setName(categoryDto.getName());
        existing.setActive(categoryDto.isActive());
        return categoryMapper.toDto(categoryRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        Category existing = getEntityOrThrow(id);
        categoryRepository.delete(existing);
    }

    @Override
    public CategoryDto getById(Long id) {
        return categoryMapper.toDto(getEntityOrThrow(id));
    }

    @Override
    public List<CategoryDto> getAll() {
        return categoryMapper.toDtoList(categoryRepository.findAll());
    }
}
