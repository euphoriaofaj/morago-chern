package com.morago.backend.mapper;

import com.morago.backend.dto.DepositDto;
import com.morago.backend.entity.Deposit;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DepositMapper {

    @Mapping(source = "user.id", target = "userId")
    DepositDto toDto(Deposit deposit);

    @Mapping(source = "userId", target = "user.id")
    Deposit toEntity(DepositDto depositDto);
}

