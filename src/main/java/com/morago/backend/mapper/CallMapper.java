package com.morago.backend.mapper;

import com.morago.backend.dto.CallDto;
import com.morago.backend.entity.Call;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ThemeMapper.class})
public interface CallMapper {

    CallMapper INSTANCE = Mappers.getMapper(CallMapper.class);

    @Mapping(target = "callerId", source = "caller.id")
    @Mapping(target = "recipientId", source = "recipient.id")
    @Mapping(target = "themeId", source = "theme.id")
    CallDto toDto(Call call);

    @Mapping(target = "caller.id", source = "callerId")
    @Mapping(target = "recipient.id", source = "recipientId")
    @Mapping(target = "theme.id", source = "themeId")
    Call toEntity(CallDto dto);
}
