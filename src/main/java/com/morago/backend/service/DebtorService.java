package com.morago.backend.service;

import com.morago.backend.dto.DebtorDto;

import java.util.List;

public interface DebtorService {
    DebtorDto create(DebtorDto debtorDto);
    DebtorDto getById(Long id);
    List<DebtorDto> getAll();
    DebtorDto update(Long id, DebtorDto debtorDto);
    void delete(Long id);
}
