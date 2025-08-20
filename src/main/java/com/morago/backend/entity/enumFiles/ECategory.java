package com.morago.backend.entity.enumFiles;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ECategory {
    WORK("Работа"),
    MEDICINE("Медицина"),
    AUTO("Авто"),
    SERVICES("Услуги"),
    BUSINESS("Бизнес"),
    GOVERNMENT("Государственные учреждения"),
    LAW("Закон"),
    EMERGENCY("Экстренный вызов");

    private final String label;
}
