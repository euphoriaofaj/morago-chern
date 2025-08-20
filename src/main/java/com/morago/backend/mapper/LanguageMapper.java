package com.morago.backend.mapper;

import com.morago.backend.dto.LanguageDto;
import com.morago.backend.entity.Language;
import com.morago.backend.entity.TranslatorProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface LanguageMapper {

    LanguageMapper INSTANCE = Mappers.getMapper(LanguageMapper.class);

    @Mapping(target = "translatorProfileIds", source = "translatorProfiles", qualifiedByName = "mapTranslatorProfilesToIds")
    LanguageDto toDto(Language language);

    @Mapping(target = "translatorProfiles", ignore = true)
    Language toEntity(LanguageDto dto);

    @Named("mapTranslatorProfilesToIds")
    default List<Long> mapTranslatorProfilesToIds(Set<TranslatorProfile> translatorProfiles) {
        if (translatorProfiles == null) return null;
        return translatorProfiles.stream()
                .map(TranslatorProfile::getId)
                .collect(Collectors.toList());
    }
}
