package com.morago.backend.service;

import com.morago.backend.dto.DebtorDto;
import com.morago.backend.entity.Debtor;
import com.morago.backend.exception.ResourceNotFoundException;
import com.morago.backend.mapper.DebtorMapper;
import com.morago.backend.repository.DebtorRepository;
import com.morago.backend.service.DebtorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DebtorServiceImpl implements DebtorService {

    private final DebtorRepository debtorRepository;
    private final DebtorMapper debtorMapper;

    @Override
    public DebtorDto create(DebtorDto debtorDto) {
        Debtor debtor = debtorMapper.toEntity(debtorDto);
        return debtorMapper.toDto(debtorRepository.save(debtor));
    }

    @Override
    public DebtorDto getById(Long id) {
        Debtor debtor = findDebtorOrThrow(id);
        return debtorMapper.toDto(debtor);
    }

    @Override
    public List<DebtorDto> getAll() {
        return debtorRepository.findAll()
                .stream()
                .map(debtorMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public DebtorDto update(Long id, DebtorDto debtorDto) {
        Debtor debtor = findDebtorOrThrow(id);

        debtor.setAccountHolder(debtorDto.getAccountHolder());
        debtor.setNameOfBank(debtorDto.getNameOfBank());
        debtor.setPaid(debtorDto.isPaid());

        return debtorMapper.toDto(debtorRepository.save(debtor));
    }

    @Override
    public void delete(Long id) {
        Debtor debtor = findDebtorOrThrow(id);
        debtorRepository.delete(debtor);
    }

    private Debtor findDebtorOrThrow(Long id) {
        return debtorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Debtor not found with id: " + id));
    }
}
