package com.morago.backend.dto;

import com.morago.backend.entity.enumFiles.EStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawalDto {

    private Long id;

    private Long userId;

    private String accountNumber;

    private String accountHolder;

    private String nameOfBank;

    private BigDecimal sumDecimal;

    private EStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
