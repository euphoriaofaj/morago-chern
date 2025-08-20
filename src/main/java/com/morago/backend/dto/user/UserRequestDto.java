package com.morago.backend.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

    @NotBlank(message = "Phone number is required")
    @Size(max = 20)
    private String username;

    private String password;

    @Size(max = 200)
    private String firstName;

    @Size(max = 200)
    private String lastName;

    private BigDecimal balance;

    private boolean isActive = true;

    private Byte onBoardingStatus;

    private Set<String> roles;
}
