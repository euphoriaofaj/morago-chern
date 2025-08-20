package com.morago.backend.service;

import com.morago.backend.dto.NotificationDto;

import java.util.List;

public interface NotificationService {
    NotificationDto create(NotificationDto dto);
    NotificationDto update(Long id, NotificationDto dto);
    NotificationDto getById(Long id);
    List<NotificationDto> getAll();
    void delete(Long id);
}
