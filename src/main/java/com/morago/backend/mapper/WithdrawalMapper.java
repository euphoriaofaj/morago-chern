package com.morago.backend.mapper;

import com.morago.backend.dto.WithdrawalDto;
import com.morago.backend.entity.User;
import com.morago.backend.entity.Withdrawal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface WithdrawalMapper {

    WithdrawalMapper INSTANCE = Mappers.getMapper(WithdrawalMapper.class);

    @Mapping(source = "user.id", target = "userId")
    WithdrawalDto toDto(Withdrawal withdrawal);

    @Mapping(source = "userId", target = "user")
    Withdrawal toEntity(WithdrawalDto withdrawalDto);

    // Helper method for mapping userId to User
    default User map(Long userId) {
        if (userId == null) return null;
        User user = new User();
        user.setId(userId);
        return user;
    }

    // Helper method for mapping User to userId
    default Long map(User user) {
        return user != null ? user.getId() : null;
    }
}
