package com.morago.backend.mapper;

import com.morago.backend.dto.ThemeDto;
import com.morago.backend.entity.Theme;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {TranslatorProfileMapper.class})
public interface ThemeMapper {

    ThemeMapper INSTANCE = Mappers.getMapper(ThemeMapper.class);

    @Mapping(target = "translators", source = "translators")
    ThemeDto toDto(Theme theme);

    @Mapping(target = "translators", source = "translators")
    Theme toEntity(ThemeDto dto);
}













//package com.morago.backend.mapper;
//
//import com.morago.backend.dto.ThemeDto;
//import com.morago.backend.dto.TranslatorProfileDto;
//import com.morago.backend.entity.Category;
//import com.morago.backend.entity.File;
//import com.morago.backend.entity.Theme;
//import com.morago.backend.entity.TranslatorProfile;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.factory.Mappers;
//
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Mapper(componentModel = "spring", uses = {TranslatorProfileMapper.class})
//public interface ThemeMapper {
//
//    ThemeMapper INSTANCE = Mappers.getMapper(ThemeMapper.class);
//
//
//    @Mapping(source = "category.id", target = "categoryId")
//    @Mapping(source = "icon.id", target = "iconFileId")
//    @Mapping(target = "translatorProfileIds", expression = "java(mapTranslatorIds(theme.getTranslators()))")
//    @Mapping(target = "translators", expression = "java(mapTranslatorDtos(theme.getTranslators()))")
//    ThemeDto toDto(Theme theme);
//
//
//    @Mapping(source = "categoryId", target = "category")
//    @Mapping(source = "iconFileId", target = "icon")
//    @Mapping(target = "translators", expression = "java(mapTranslatorEntities(dto.getTranslatorProfileIds()))")
//    Theme toEntity(ThemeDto dto);
//
//
//    default Category map(Long categoryId) {
//        if (categoryId == null) return null;
//        Category category = new Category();
//        category.setId(categoryId);
//        return category;
//    }
//
//    default File mapFile(Long fileId) {
//        if (fileId == null) return null;
//        File file = new File();
//        file.setId(fileId);
//        return file;
//    }
//
//    default List<Long> mapTranslatorIds(Set<TranslatorProfile> translators) {
//        return translators == null ? null :
//                translators.stream().map(TranslatorProfile::getId).collect(Collectors.toList());
//    }
//
//    default List<TranslatorProfileDto> mapTranslatorDtos(Set<TranslatorProfile> translators) {
//        return translators == null ? null :
//                translators.stream().map(TranslatorProfileMapper.INSTANCE::toDto).collect(Collectors.toList());
//    }
//
//    default Set<TranslatorProfile> mapTranslatorEntities(List<Long> ids) {
//        return ids == null ? null :
//                ids.stream().map(id -> {
//                    TranslatorProfile tp = new TranslatorProfile();
//                    tp.setId(id);
//                    return tp;
//                }).collect(Collectors.toSet());
//    }
//}
