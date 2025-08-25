package com.morago.backend.mapper;

import com.morago.backend.dto.request.TranslatorProfileCreateRequest;
import com.morago.backend.dto.request.TranslatorProfileUpdateRequest;
import com.morago.backend.dto.response.TranslatorProfileResponse;
import com.morago.backend.dto.response.TranslatorProfileSummaryResponse;
import com.morago.backend.entity.Language;
import com.morago.backend.entity.Theme;
import com.morago.backend.entity.TranslatorProfile;
import com.morago.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TranslatorProfileMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.username", target = "username")
    @Mapping(target = "averageRating", ignore = true) // Will be calculated separately
    @Mapping(target = "totalCalls", ignore = true) // Will be calculated separately
    @Mapping(target = "totalRatings", ignore = true) // Will be calculated separately
    TranslatorProfileResponse toResponse(TranslatorProfile profile);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "fullName", expression = "java(getFullName(profile.getUser()))")
    @Mapping(target = "languageNames", source = "languages", qualifiedByName = "languagesToNames")
    @Mapping(target = "themeNames", source = "themes", qualifiedByName = "themesToNames")
    @Mapping(target = "averageRating", ignore = true) // Will be calculated separately
    @Mapping(target = "totalCalls", ignore = true) // Will be calculated separately
    @Mapping(target = "inCall", ignore = true) // Will be calculated separately
    TranslatorProfileSummaryResponse toSummaryResponse(TranslatorProfile profile);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "languages", ignore = true)
    @Mapping(target = "themes", ignore = true)
    TranslatorProfile toEntity(TranslatorProfileCreateRequest request);

    @Named("languagesToNames")
    default Set<String> languagesToNames(Set<Language> languages) {
        if (languages == null) return null;
        return languages.stream()
                .map(Language::getName)
                .collect(Collectors.toSet());
    }

    @Named("themesToNames")
    default Set<String> themesToNames(Set<Theme> themes) {
        if (themes == null) return null;
        return themes.stream()
                .map(Theme::getName)
                .collect(Collectors.toSet());
    }

    default String getFullName(User user) {
        if (user == null) return null;
        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
        String lastName = user.getLastName() != null ? user.getLastName() : "";
        return (firstName + " " + lastName).trim();
    }
}
