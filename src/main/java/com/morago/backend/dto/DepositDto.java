package com.morago.backend.dto;

import com.morago.backend.entity.enumFiles.EStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositDto {

    private Long id;
    private Long userId;
    private String accountHolder;
    private String nameOfBank;
    private BigDecimal coinDecimal;
    private BigDecimal wonDecimal;
    private EStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
