package com.morago.backend.service;

import com.morago.backend.dto.FileDto;

import java.util.List;

public interface FileService {
    FileDto create(FileDto dto);
    FileDto update(Long id, FileDto dto);
    void delete(Long id);
    FileDto getById(Long id);
    List<FileDto> getAll();
}
