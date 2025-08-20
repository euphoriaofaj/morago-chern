package com.morago.backend.mapper;

import com.morago.backend.dto.RatingDto;
import com.morago.backend.entity.Rating;
import com.morago.backend.entity.User;
import com.morago.backend.entity.TranslatorProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RatingMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "translatorProfileId", source = "translator.id")
    RatingDto toDto(Rating rating);

    @Mapping(target = "user", expression = "java(toUser(dto.getUserId()))")
    @Mapping(target = "translator", expression = "java(toTranslator(dto.getTranslatorProfileId()))")
    @Mapping(target = "id", ignore = true)
    Rating toEntity(RatingDto dto);

    default User toUser(Long userId) {
        if (userId == null) return null;
        User user = new User();
        user.setId(userId);
        return user;
    }

    default TranslatorProfile toTranslator(Long translatorId) {
        if (translatorId == null) return null;
        TranslatorProfile translator = new TranslatorProfile();
        translator.setId(translatorId);
        return translator;
    }
}
