package com.morago.backend.service;

import com.morago.backend.dto.FileDto;
import com.morago.backend.entity.File;
import com.morago.backend.exception.ResourceNotFoundException;
import com.morago.backend.mapper.FileMapper;
import com.morago.backend.repository.FileRepository;
import com.morago.backend.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final FileMapper fileMapper;

    // === Exception helper (standardized) ===
    private File getFileOrThrow(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + id));
    }

    @Override
    public FileDto create(FileDto dto) {
        File file = fileMapper.toEntity(dto);
        File saved = fileRepository.save(file);
        return fileMapper.toDto(saved);
    }

    @Override
    public FileDto update(Long id, FileDto dto) {
        File existing = getFileOrThrow(id);

        existing.setOriginalTitle(dto.getOriginalTitle());
        existing.setPath(dto.getPath());
        existing.setType(dto.getType());

        if (dto.getUserId() != null) {
            existing.setUser(fileMapper.map(dto.getUserId()));
        }

        File updated = fileRepository.save(existing);
        return fileMapper.toDto(updated);
    }

    @Override
    public void delete(Long id) {
        File file = getFileOrThrow(id);
        fileRepository.delete(file);
    }

    @Override
    public FileDto getById(Long id) {
        return fileMapper.toDto(getFileOrThrow(id));
    }

    @Override
    public List<FileDto> getAll() {
        return fileRepository.findAll().stream()
                .map(fileMapper::toDto)
                .toList();
    }
}
