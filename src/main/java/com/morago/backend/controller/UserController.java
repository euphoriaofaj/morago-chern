package com.morago.backend.controller;

import com.morago.backend.dto.user.UserRequestDto;
import com.morago.backend.dto.user.UserResponseDto;
import com.morago.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing users.
 * Provides endpoints for user CRUD operations with role-based access control.
 */
@Tag(name = "Users", description = "User management endpoints")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    /**
     * Creates a new user.
     * Only accessible by ADMIN.
     */
    @Operation(
            summary = "Create user",
            description = "Creates a new user with appropriate profiles. Only admins can create users."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "409", description = "Username already exists")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto dto) {
        log.info("Creating user with username: {}", dto.getUsername());
        
        UserResponseDto created = userService.createUser(dto);
        
        log.info("User created successfully with ID: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Retrieves all users with pagination.
     * Only accessible by ADMIN.
     */
    @Operation(
            summary = "Get all users",
            description = "Retrieves paginated list of all users. Only admins can access this endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        
        log.debug("Retrieving all users with pagination");
        
        Page<UserResponseDto> users = userService.getAllUsers(pageable);
        
        log.info("Retrieved {} users out of {} total", 
                users.getNumberOfElements(), users.getTotalElements());
        
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves a specific user by ID.
     * Accessible by ADMIN or the user themselves.
     */
    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a specific user by their ID. Admins can access any user, users can only access their own data."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        log.debug("Retrieving user with ID: {}", id);
        
        UserResponseDto user = userService.getUser(id);
        
        log.debug("User found: {}", user.getUsername());
        return ResponseEntity.ok(user);
    }

    /**
     * Updates an existing user.
     * Accessible by ADMIN or the user themselves.
     */
    @Operation(
            summary = "Update user",
            description = "Updates an existing user. Admins can update any user, users can only update their own data."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id, 
            @Valid @RequestBody UserRequestDto dto) {
        
        log.info("Updating user with ID: {}", id);
        
        UserResponseDto updated = userService.updateUser(id, dto);
        
        log.info("User updated successfully: {}", updated.getUsername());
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a user.
     * Only accessible by ADMIN.
     */
    @Operation(
            summary = "Delete user",
            description = "Deletes a user and all associated profiles. Only admins can delete users."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with ID: {}", id);
        
        userService.deleteUser(id);
        
        log.info("User deleted successfully with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Gets current user's profile.
     * Accessible by any authenticated user.
     */
    @Operation(
            summary = "Get current user profile",
            description = "Retrieves the profile of the currently authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Current user profile retrieved"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        // This would need to be implemented to get current user from security context
        // For now, returning a placeholder response
        log.debug("Retrieving current user profile");
        
        // Implementation would extract user ID from SecurityContext
        // Long currentUserId = getCurrentUserIdFromSecurityContext();
        // return ResponseEntity.ok(userService.getUser(currentUserId));
        
        return ResponseEntity.ok().build();
    }
}