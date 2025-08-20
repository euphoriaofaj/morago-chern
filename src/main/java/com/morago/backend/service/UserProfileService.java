package com.morago.backend.service;

import com.morago.backend.dto.UserProfileDto;
import java.util.List;

public interface UserProfileService {
    UserProfileDto createUserProfile(UserProfileDto dto);
    UserProfileDto getUserProfileById(Long id);
    List<UserProfileDto> getAllUserProfiles();
    UserProfileDto updateUserProfile(Long id, UserProfileDto dto);
    void deleteUserProfile(Long id);
}
