package com.morago.backend.service;

import com.morago.backend.dto.UserProfileDto;
import com.morago.backend.entity.UserProfile;
import com.morago.backend.mapper.UserProfileMapper;
import com.morago.backend.repository.UserProfileRepository;
import com.morago.backend.repository.UserRepository;
import com.morago.backend.exception.ResourceNotFoundException;
import com.morago.backend.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final UserProfileMapper mapper;

    private <T> T findOrThrow(java.util.Optional<T> optional, String entityName, Long id) {
        return optional.orElseThrow(() -> new ResourceNotFoundException(entityName + " not found with id " + id));
    }

    @Override
    public UserProfileDto createUserProfile(UserProfileDto dto) {
        UserProfile userProfile = mapper.toEntity(dto);

        if (dto.getUserId() != null) {
            userProfile.setUser(findOrThrow(userRepository.findById(dto.getUserId()), "User", dto.getUserId()));
        }

        return mapper.toDto(userProfileRepository.save(userProfile));
    }

    @Override
    public UserProfileDto getUserProfileById(Long id) {
        UserProfile userProfile = findOrThrow(userProfileRepository.findById(id), "UserProfile", id);
        return mapper.toDto(userProfile);
    }

    @Override
    public List<UserProfileDto> getAllUserProfiles() {
        return userProfileRepository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public UserProfileDto updateUserProfile(Long id, UserProfileDto dto) {
        UserProfile userProfile = findOrThrow(userProfileRepository.findById(id), "UserProfile", id);

        userProfile.setFreeCallMade(dto.isFreeCallMade());

        if (dto.getUserId() != null) {
            userProfile.setUser(findOrThrow(userRepository.findById(dto.getUserId()), "User", dto.getUserId()));
        }

        return mapper.toDto(userProfileRepository.save(userProfile));
    }

    @Override
    public void deleteUserProfile(Long id) {
        if (!userProfileRepository.existsById(id)) {
            throw new ResourceNotFoundException("UserProfile not found with id " + id);
        }
        userProfileRepository.deleteById(id);
    }
}
