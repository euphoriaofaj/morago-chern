package com.morago.backend.service;


import com.morago.backend.dto.user.UserRequestDto;
import com.morago.backend.dto.user.UserResponseDto;
import com.morago.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
    User findByUsernameOrThrow(String username);
    UserResponseDto createUser(UserRequestDto dto);
    UserResponseDto getUser(Long id);
    Page<UserResponseDto> getAllUsers(Pageable pageable);
    UserResponseDto updateUser(Long id, UserRequestDto dto);
    void deleteUser(Long id);
}
