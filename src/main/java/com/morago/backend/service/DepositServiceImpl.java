package com.morago.backend.service;

import com.morago.backend.dto.DepositDto;
import com.morago.backend.entity.Deposit;
import com.morago.backend.mapper.DepositMapper;
import com.morago.backend.repository.DepositRepository;
import com.morago.backend.repository.UserRepository;
import com.morago.backend.exception.ResourceNotFoundException;
import com.morago.backend.service.DepositService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DepositServiceImpl implements DepositService {

    private final DepositRepository depositRepository;
    private final UserRepository userRepository;
    private final DepositMapper mapper;

    private <T> T findOrThrow(java.util.Optional<T> optional, String entityName, Long id) {
        return optional.orElseThrow(() -> new ResourceNotFoundException(entityName + " not found with id " + id));
    }

    @Override
    public DepositDto createDeposit(DepositDto dto) {
        Deposit deposit = mapper.toEntity(dto);

        if (dto.getUserId() != null) {
            deposit.setUser(findOrThrow(userRepository.findById(dto.getUserId()), "User", dto.getUserId()));
        }

        return mapper.toDto(depositRepository.save(deposit));
    }

    @Override
    public DepositDto getDepositById(Long id) {
        return mapper.toDto(findOrThrow(depositRepository.findById(id), "Deposit", id));
    }

    @Override
    public List<DepositDto> getAllDeposits() {
        return depositRepository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public DepositDto updateDeposit(Long id, DepositDto dto) {
        Deposit deposit = findOrThrow(depositRepository.findById(id), "Deposit", id);

        deposit.setAccountHolder(dto.getAccountHolder());
        deposit.setNameOfBank(dto.getNameOfBank());
        deposit.setCoinDecimal(dto.getCoinDecimal());
        deposit.setWonDecimal(dto.getWonDecimal());
        deposit.setStatus(dto.getStatus());

        if (dto.getUserId() != null) {
            deposit.setUser(findOrThrow(userRepository.findById(dto.getUserId()), "User", dto.getUserId()));
        }

        return mapper.toDto(depositRepository.save(deposit));
    }

    @Override
    public void deleteDeposit(Long id) {
        if (!depositRepository.existsById(id)) {
            throw new ResourceNotFoundException("Deposit not found with id " + id);
        }
        depositRepository.deleteById(id);
    }
}
