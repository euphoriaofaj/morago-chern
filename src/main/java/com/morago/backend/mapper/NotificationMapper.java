package com.morago.backend.mapper;

import com.morago.backend.dto.NotificationDto;
import com.morago.backend.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "user.id", target = "userId")
    NotificationDto toDto(Notification notification);

    @Mapping(source = "userId", target = "user.id")
    Notification toEntity(NotificationDto dto);
}
