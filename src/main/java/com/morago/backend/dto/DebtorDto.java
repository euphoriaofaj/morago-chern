package com.morago.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtorDto {
    private Long id;
    private Long userId;
    private String accountHolder;
    private String nameOfBank;
    private boolean isPaid;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
