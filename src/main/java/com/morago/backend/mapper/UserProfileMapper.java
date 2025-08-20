package com.morago.backend.mapper;

import com.morago.backend.dto.UserProfileDto;
import com.morago.backend.entity.UserProfile;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(source = "user.id", target = "userId")
    UserProfileDto toDto(UserProfile userProfile);

    @Mapping(source = "userId", target = "user.id")
    UserProfile toEntity(UserProfileDto userProfileDto);
}
