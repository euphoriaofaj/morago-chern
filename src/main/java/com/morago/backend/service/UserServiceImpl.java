package com.morago.backend.service;

import com.morago.backend.dto.user.UserRequestDto;
import com.morago.backend.dto.user.UserResponseDto;
import com.morago.backend.entity.Role;
import com.morago.backend.entity.User;
import com.morago.backend.entity.UserProfile;
import com.morago.backend.entity.TranslatorProfile;
import com.morago.backend.entity.enumFiles.Roles;
import com.morago.backend.exception.UserNotFoundException;
import com.morago.backend.mapper.UserMapper;
import com.morago.backend.repository.RefreshTokenRepository;
import com.morago.backend.repository.RoleRepository;
import com.morago.backend.repository.UserRepository;
import com.morago.backend.repository.UserProfileRepository;
import com.morago.backend.repository.TranslatorProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service implementation for managing user operations.
 * Handles user CRUD operations, authentication, and role management.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserProfileRepository userProfileRepository;
    private final TranslatorProfileRepository translatorProfileRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Finds a user by username.
     * 
     * @param username the username to search for
     * @return Optional containing the User if found
     */
    @Override
    public Optional<User> findByUsername(String username) {
        log.debug("Searching for user with username: {}", username);
        return userRepository.findByUsername(username);
    }

    /**
     * Finds a user by username or throws exception if not found.
     * 
     * @param username the username to search for
     * @return the User entity
     * @throws UserNotFoundException if user is not found
     */
    @Override
    public User findByUsernameOrThrow(String username) {
        return findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found with username: {}", username);
                    return new UserNotFoundException(username);
                });
    }

    /**
     * Creates a new user with the provided information.
     * 
     * @param dto the user creation request data
     * @return UserResponseDto containing the created user information
     * @throws IllegalArgumentException if username already exists or password is missing
     */
    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto dto) {
        log.debug("Creating new user with username: {}", dto.getUsername());
        
        validateUserCreation(dto);
        
        User user = buildNewUser(dto);
        User savedUser = userRepository.save(user);
        
        // Automatically create appropriate profile based on user roles
        createUserProfiles(savedUser);
        
        log.info("User created successfully with ID: {} and username: {}", 
                savedUser.getId(), savedUser.getUsername());
        
        return userMapper.toResponseDto(savedUser);
    }

    /**
     * Retrieves a user by their ID.
     * 
     * @param id the user ID
     * @return UserResponseDto containing user information
     * @throws UserNotFoundException if user is not found
     */
    @Override
    public UserResponseDto getUser(Long id) {
        log.debug("Retrieving user with ID: {}", id);
        
        return userRepository.findById(id)
                .map(user -> {
                    log.debug("User found: {}", user.getUsername());
                    return userMapper.toResponseDto(user);
                })
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", id);
                    return new UserNotFoundException(String.valueOf(id));
                });
    }

    /**
     * Retrieves all users in the system.
     * 
     * @return Page of UserResponseDto containing all users
     */
    @Override
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        log.debug("Retrieving all users with pagination");
        
        Page<User> users = userRepository.findAll(pageable);
        log.info("Found {} users out of {} total", users.getNumberOfElements(), users.getTotalElements());
        
        return users.map(userMapper::toResponseDto);
    }

    /**
     * Updates an existing user with new information.
     * 
     * @param id the user ID to update
     * @param dto the updated user information
     * @return UserResponseDto containing updated user information
     * @throws UserNotFoundException if user is not found
     */
    @Override
    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto dto) {
        log.debug("Updating user with ID: {}", id);
        
        User existingUser = findUserByIdOrThrow(id);
        updateUserFields(existingUser, dto);
        
        User updatedUser = userRepository.save(existingUser);
        
        log.info("User updated successfully: {}", updatedUser.getUsername());
        return userMapper.toResponseDto(updatedUser);
    }

    /**
     * Deletes a user and all associated refresh tokens.
     * 
     * @param id the user ID to delete
     * @throws UserNotFoundException if user is not found
     */
    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.debug("Deleting user with ID: {}", id);
        
        User user = findUserByIdOrThrow(id);
        String username = user.getUsername();
        
        // Clean up associated refresh tokens first
        refreshTokenRepository.deleteByUser(user);
        
        // Profiles will be automatically deleted due to cascade settings in User entity
        userRepository.delete(user);
        
        log.info("User deleted successfully: {}", username);
    }
    
    /**
     * Creates appropriate profiles for the user based on their roles.
     */
    private void createUserProfiles(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
        
        // Create UserProfile for all users (basic profile)
        if (!userProfileRepository.existsByUserId(user.getId())) {
            UserProfile userProfile = UserProfile.builder()
                    .user(user)
                    .isFreeCallMade(false)
                    .build();
            userProfileRepository.save(userProfile);
            log.info("UserProfile created for user ID: {}", user.getId());
        }
        
        // Create TranslatorProfile if user has TRANSLATOR role
        if (roleNames.contains(Roles.ROLE_TRANSLATOR.name()) && 
            !translatorProfileRepository.existsByUserId(user.getId())) {
            
            TranslatorProfile translatorProfile = TranslatorProfile.builder()
                    .user(user)
                    .isAvailable(false) // Default to not available until they complete their profile
                    .isOnline(false)
                    .build();
            translatorProfileRepository.save(translatorProfile);
            log.info("TranslatorProfile created for user ID: {}", user.getId());
        }
    }
    
    /**
     * Validates user creation request.
     */
    private void validateUserCreation(UserRequestDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            log.warn("Attempt to create user with existing username: {}", dto.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }
        
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            log.warn("Attempt to create user without password");
            throw new IllegalArgumentException("Password is required for new user");
        }
    }
    
    /**
     * Builds a new User entity from the request DTO.
     */
    private User buildNewUser(UserRequestDto dto) {
        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(resolveRoles(dto.getRoles()));
        return user;
    }
    
    /**
     * Finds user by ID or throws exception.
     */
    private User findUserByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with ID: {}", id);
                    return new UserNotFoundException(String.valueOf(id));
                });
    }
    
    /**
     * Updates user fields from DTO.
     */
    private void updateUserFields(User user, UserRequestDto dto) {
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setBalance(dto.getBalance());
        user.setActive(dto.isActive());
        user.setOnBoardingStatus(dto.getOnBoardingStatus());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (dto.getRoles() != null) {
            user.setRoles(resolveRoles(dto.getRoles()));
        }
    }

    /**
     * Resolves role names to Role entities.
     * 
     * @param roleNames set of role name strings
     * @return set of Role entities
     * @throws IllegalArgumentException if any role is not found
     */
    private Set<Role> resolveRoles(Set<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return new HashSet<>();
        }
        
        return roleNames.stream()
                .map(name -> roleRepository.findByNameEnum(name)
                        .orElseThrow(() -> {
                            log.warn("Role not found: {}", name);
                            return new IllegalArgumentException("Role not found: " + name);
                        }))
                .collect(Collectors.toSet());
    }
}