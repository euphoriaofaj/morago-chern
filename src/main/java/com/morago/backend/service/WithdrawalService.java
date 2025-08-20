package com.morago.backend.service;

import com.morago.backend.dto.WithdrawalDto;
import java.util.List;

public interface WithdrawalService {
    WithdrawalDto createWithdrawal(WithdrawalDto dto);
    WithdrawalDto getWithdrawalById(Long id);
    List<WithdrawalDto> getAllWithdrawals();
    WithdrawalDto updateWithdrawal(Long id, WithdrawalDto dto);
    void deleteWithdrawal(Long id);
}
