package com.morago.backend.service;

import com.morago.backend.dto.NotificationDto;
import com.morago.backend.entity.Notification;
import com.morago.backend.exception.ResourceNotFoundException;
import com.morago.backend.mapper.NotificationMapper;
import com.morago.backend.repository.NotificationRepository;
import com.morago.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    private Notification getEntityById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id " + id));
    }

    @Override
    public NotificationDto create(NotificationDto dto) {
        Notification notification = notificationMapper.toEntity(dto);
        return notificationMapper.toDto(notificationRepository.save(notification));
    }

    @Override
    public NotificationDto update(Long id, NotificationDto dto) {
        Notification existing = getEntityById(id);
        existing.setTitle(dto.getTitle());
        existing.setText(dto.getText());
        existing.setDateTime(dto.getDateTime());
        existing.setErrorTime(dto.getErrorTime());
        existing.setRead(dto.isRead());
        return notificationMapper.toDto(notificationRepository.save(existing));
    }

    @Override
    public NotificationDto getById(Long id) {
        return notificationMapper.toDto(getEntityById(id));
    }

    @Override
    public List<NotificationDto> getAll() {
        return notificationRepository.findAll()
                .stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        Notification notification = getEntityById(id);
        notificationRepository.delete(notification);
    }
}
