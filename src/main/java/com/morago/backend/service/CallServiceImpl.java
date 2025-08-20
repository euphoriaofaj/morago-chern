package com.morago.backend.service;

import com.morago.backend.dto.CallDto;
import com.morago.backend.entity.Call;
import com.morago.backend.mapper.CallMapper;
import com.morago.backend.repository.CallRepository;
import com.morago.backend.repository.UserRepository;
import com.morago.backend.repository.ThemeRepository;
import com.morago.backend.exception.ResourceNotFoundException;
import com.morago.backend.service.CallService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CallServiceImpl implements CallService {

    private final CallRepository callRepository;
    private final UserRepository userRepository;
    private final ThemeRepository themeRepository;
    private final CallMapper mapper;

    private <T> T findOrThrow(java.util.Optional<T> optional, String entityName, Long id) {
        return optional.orElseThrow(() -> new ResourceNotFoundException(entityName + " not found with id " + id));
    }

    @Override
    public CallDto createCall(CallDto dto) {
        Call call = mapper.toEntity(dto);

        if (dto.getCallerId() != null) {
            call.setCaller(findOrThrow(userRepository.findById(dto.getCallerId()), "User", dto.getCallerId()));
        }
        if (dto.getRecipientId() != null) {
            call.setRecipient(findOrThrow(userRepository.findById(dto.getRecipientId()), "User", dto.getRecipientId()));
        }
        if (dto.getThemeId() != null) {
            call.setTheme(findOrThrow(themeRepository.findById(dto.getThemeId()), "Theme", dto.getThemeId()));
        }

        return mapper.toDto(callRepository.save(call));
    }

    @Override
    public CallDto getCallById(Long id) {
        return mapper.toDto(findOrThrow(callRepository.findById(id), "Call", id));
    }

    @Override
    public List<CallDto> getAllCalls() {
        return callRepository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public CallDto updateCall(Long id, CallDto dto) {
        Call call = findOrThrow(callRepository.findById(id), "Call", id);

        call.setDuration(dto.getDuration());
        call.setStatus(dto.isStatus());
        call.setSumDecimal(dto.getSumDecimal());
        call.setCommission(dto.getCommission());
        call.setTranslatorHasJoined(dto.isTranslatorHasJoined());
        call.setUserHasRated(dto.isUserHasRated());
        call.setCallStatus(dto.getCallStatus());
//        call.setIsEndCall(dto.isEndCall());
        call.setChannelName(dto.getChannelName());

        if (dto.getCallerId() != null) {
            call.setCaller(findOrThrow(userRepository.findById(dto.getCallerId()), "User", dto.getCallerId()));
        }
        if (dto.getRecipientId() != null) {
            call.setRecipient(findOrThrow(userRepository.findById(dto.getRecipientId()), "User", dto.getRecipientId()));
        }
        if (dto.getThemeId() != null) {
            call.setTheme(findOrThrow(themeRepository.findById(dto.getThemeId()), "Theme", dto.getThemeId()));
        }

        return mapper.toDto(callRepository.save(call));
    }

    @Override
    public void deleteCall(Long id) {
        if (!callRepository.existsById(id)) {
            throw new ResourceNotFoundException("Call not found with id " + id);
        }
        callRepository.deleteById(id);
    }
}
