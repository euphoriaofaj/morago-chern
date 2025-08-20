package com.morago.backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private BigDecimal balance;
    private boolean isActive;
    private Byte onBoardingStatus;
    private Set<String> roles;
}
