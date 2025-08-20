package com.morago.backend.service;

import com.morago.backend.dto.WithdrawalDto;
import com.morago.backend.entity.Withdrawal;
import com.morago.backend.mapper.WithdrawalMapper;
import com.morago.backend.repository.WithdrawalRepository;
import com.morago.backend.repository.UserRepository;
import com.morago.backend.exception.ResourceNotFoundException;
import com.morago.backend.service.WithdrawalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WithdrawalServiceImpl implements WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;
    private final UserRepository userRepository;
    private final WithdrawalMapper mapper;

    private <T> T findOrThrow(java.util.Optional<T> optional, String entityName, Long id) {
        return optional.orElseThrow(() -> new ResourceNotFoundException(entityName + " not found with id " + id));
    }

    @Override
    public WithdrawalDto createWithdrawal(WithdrawalDto dto) {
        Withdrawal withdrawal = mapper.toEntity(dto);

        if (dto.getUserId() != null) {
            withdrawal.setUser(findOrThrow(userRepository.findById(dto.getUserId()), "User", dto.getUserId()));
        }

        return mapper.toDto(withdrawalRepository.save(withdrawal));
    }

    @Override
    public WithdrawalDto getWithdrawalById(Long id) {
        return mapper.toDto(findOrThrow(withdrawalRepository.findById(id), "Withdrawal", id));
    }

    @Override
    public List<WithdrawalDto> getAllWithdrawals() {
        return withdrawalRepository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public WithdrawalDto updateWithdrawal(Long id, WithdrawalDto dto) {
        Withdrawal withdrawal = findOrThrow(withdrawalRepository.findById(id), "Withdrawal", id);

        withdrawal.setAccountNumber(dto.getAccountNumber());
        withdrawal.setAccountHolder(dto.getAccountHolder());
        withdrawal.setNameOfBank(dto.getNameOfBank());
        withdrawal.setSumDecimal(dto.getSumDecimal());
        withdrawal.setStatus(dto.getStatus());

        if (dto.getUserId() != null) {
            withdrawal.setUser(findOrThrow(userRepository.findById(dto.getUserId()), "User", dto.getUserId()));
        }

        return mapper.toDto(withdrawalRepository.save(withdrawal));
    }

    @Override
    public void deleteWithdrawal(Long id) {
        if (!withdrawalRepository.existsById(id)) {
            throw new ResourceNotFoundException("Withdrawal not found with id " + id);
        }
        withdrawalRepository.deleteById(id);
    }
}
