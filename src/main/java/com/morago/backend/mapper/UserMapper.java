package com.morago.backend.mapper;

import com.morago.backend.dto.user.UserRequestDto;
import com.morago.backend.dto.user.UserResponseDto;
import com.morago.backend.entity.Role;
import com.morago.backend.entity.User;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserRequestDto dto);


    @Mapping(target = "roles", expression = "java(mapRolesToNames(user.getRoles()))")
    UserResponseDto toResponseDto(User user);

    default Set<String> mapRolesToNames(Set<Role> roles) {
        if (roles == null) return null;
        return roles.stream()
                .map(role -> role.getName().name()) // Assuming RoleName enum
                .collect(Collectors.toSet());
    }
}













//package com.morago.backend.mapper;
//
//import com.morago.backend.dto.tokens.UserDto;
//import com.morago.backend.entity.User;
//import com.morago.backend.entity.Role;
//import org.mapstruct.*;
//
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Mapper(componentModel = "spring")
//public interface UserMapper {
//
////    @Mapping(target = "roles", expression = "java(mapRolesToStrings(user.getRoles()))")
//    UserDto toDto(User user);
//
////    @InheritInverseConfiguration
////    @Mapping(target = "roles", ignore = true)
//    User toEntity(UserDto dto);
//
////    default Set<String> mapRolesToStrings(Set<Role> roles) {
////        if (roles == null) return null;
////        return roles.stream()
////                .map(role -> roles.getRoles().getName())
////                .collect(Collectors.toSet());
////    }
//}

