package com.morago.backend.service;

import com.morago.backend.dto.DepositDto;
import java.util.List;

public interface DepositService {
    DepositDto createDeposit(DepositDto dto);
    DepositDto getDepositById(Long id);
    List<DepositDto> getAllDeposits();
    DepositDto updateDeposit(Long id, DepositDto dto);
    void deleteDeposit(Long id);
}
