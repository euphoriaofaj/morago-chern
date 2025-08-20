package com.morago.backend.mapper;

import com.morago.backend.dto.DebtorDto;
import com.morago.backend.entity.Debtor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DebtorMapper {

    @Mapping(target = "userId", source = "user.id")
    DebtorDto toDto(Debtor debtor);

    @Mapping(target = "user.id", source = "userId")
    Debtor toEntity(DebtorDto dto);
}
