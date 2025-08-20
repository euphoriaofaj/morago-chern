package com.morago.backend.mapper;

import com.morago.backend.dto.FileDto;
import com.morago.backend.entity.File;
import com.morago.backend.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FileMapper {

    @Mapping(target = "userId", source = "user.id")
    FileDto toDto(File file);

    @Mapping(target = "user", source = "userId")
    File toEntity(FileDto dto);

    default User map(Long id) {
        if (id == null) return null;
        User user = new User();
        user.setId(id);
        return user;
    }
}
