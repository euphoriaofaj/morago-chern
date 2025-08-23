package com.morago.backend.controller;

import com.morago.backend.dto.TranslatorProfileDto;
import com.morago.backend.service.TranslatorProfileService;
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
 * REST controller for managing translator profiles.
 * Provides endpoints for CRUD operations on translator profiles with role-based access control.
 */
@Tag(name = "Translator Profiles", description = "Translator profile management endpoints")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/translator-profiles")
@SecurityRequirement(name = "bearerAuth")
public class TranslatorProfileController {

    private final TranslatorProfileService translatorProfileService;

    /**
     * Creates a new translator profile.
     * Only accessible by ADMIN or the user creating their own profile.
     */
    @Operation(
            summary = "Create translator profile",
            description = "Creates a new translator profile. Only admins or the profile owner can create profiles."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Translator profile created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "409", description = "Profile already exists for this user")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('TRANSLATOR') and #dto.userId == authentication.principal.id)")
    public ResponseEntity<TranslatorProfileDto> createProfile(@Valid @RequestBody TranslatorProfileDto dto) {
        log.info("Creating translator profile for user ID: {}", dto.getUserId());
        
        TranslatorProfileDto created = translatorProfileService.create(dto);
        
        log.info("Translator profile created successfully with ID: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Retrieves all translator profiles with pagination and filtering.
     * Accessible by all authenticated users for browsing translators.
     */
    @Operation(
            summary = "Get all translator profiles",
            description = "Retrieves paginated list of translator profiles with optional filtering by availability, language, and theme."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Translator profiles retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<TranslatorProfileDto>> getAllProfiles(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable,
            @Parameter(description = "Filter by availability status") 
            @RequestParam(required = false) Boolean isAvailable,
            @Parameter(description = "Filter by online status") 
            @RequestParam(required = false) Boolean isOnline,
            @Parameter(description = "Filter by language ID") 
            @RequestParam(required = false) Long languageId,
            @Parameter(description = "Filter by theme ID") 
            @RequestParam(required = false) Long themeId,
            @Parameter(description = "Filter by Korean proficiency level") 
            @RequestParam(required = false) String levelOfKorean) {
        
        log.debug("Retrieving translator profiles with filters - available: {}, online: {}, language: {}, theme: {}", 
                isAvailable, isOnline, languageId, themeId);
        
        Page<TranslatorProfileDto> profiles = translatorProfileService.getAllWithFilters(
                pageable, isAvailable, isOnline, languageId, themeId, levelOfKorean);
        
        log.info("Retrieved {} translator profiles out of {} total", 
                profiles.getNumberOfElements(), profiles.getTotalElements());
        
        return ResponseEntity.ok(profiles);
    }

    /**
     * Retrieves a specific translator profile by ID.
     * Accessible by all authenticated users for viewing translator details.
     */
    @Operation(
            summary = "Get translator profile by ID",
            description = "Retrieves a specific translator profile by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Translator profile found"),
            @ApiResponse(responseCode = "404", description = "Translator profile not found"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TranslatorProfileDto> getProfileById(@PathVariable Long id) {
        log.debug("Retrieving translator profile with ID: {}", id);
        
        TranslatorProfileDto profile = translatorProfileService.getById(id);
        
        log.debug("Translator profile found: {}", profile.getEmail());
        return ResponseEntity.ok(profile);
    }

    /**
     * Updates an existing translator profile.
     * Only accessible by ADMIN or the profile owner.
     */
    @Operation(
            summary = "Update translator profile",
            description = "Updates an existing translator profile. Only admins or the profile owner can update profiles."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Translator profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Translator profile not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @translatorProfileService.isProfileOwner(#id, authentication.principal.username)")
    public ResponseEntity<TranslatorProfileDto> updateProfile(
            @PathVariable Long id, 
            @Valid @RequestBody TranslatorProfileDto dto) {
        
        log.info("Updating translator profile with ID: {}", id);
        
        TranslatorProfileDto updated = translatorProfileService.update(id, dto);
        
        log.info("Translator profile updated successfully: {}", updated.getEmail());
        return ResponseEntity.ok(updated);
    }

    /**
     * Updates translator availability status.
     * Only accessible by the profile owner or ADMIN.
     */
    @Operation(
            summary = "Update translator availability",
            description = "Updates the availability status of a translator. Only the profile owner or admin can update availability."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Availability updated successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Translator profile not found")
    })
    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasRole('ADMIN') or @translatorProfileService.isProfileOwner(#id, authentication.principal.username)")
    public ResponseEntity<TranslatorProfileDto> updateAvailability(
            @PathVariable Long id,
            @RequestParam Boolean isAvailable) {
        
        log.info("Updating availability for translator profile ID: {} to {}", id, isAvailable);
        
        TranslatorProfileDto updated = translatorProfileService.updateAvailability(id, isAvailable);
        
        log.info("Availability updated successfully for translator: {}", updated.getEmail());
        return ResponseEntity.ok(updated);
    }

    /**
     * Updates translator online status.
     * Only accessible by the profile owner or ADMIN.
     */
    @Operation(
            summary = "Update translator online status",
            description = "Updates the online status of a translator. Only the profile owner or admin can update online status."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Online status updated successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Translator profile not found")
    })
    @PatchMapping("/{id}/online-status")
    @PreAuthorize("hasRole('ADMIN') or @translatorProfileService.isProfileOwner(#id, authentication.principal.username)")
    public ResponseEntity<TranslatorProfileDto> updateOnlineStatus(
            @PathVariable Long id,
            @RequestParam Boolean isOnline) {
        
        log.info("Updating online status for translator profile ID: {} to {}", id, isOnline);
        
        TranslatorProfileDto updated = translatorProfileService.updateOnlineStatus(id, isOnline);
        
        log.info("Online status updated successfully for translator: {}", updated.getEmail());
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a translator profile.
     * Only accessible by ADMIN or the profile owner.
     */
    @Operation(
            summary = "Delete translator profile",
            description = "Deletes a translator profile. Only admins or the profile owner can delete profiles."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Translator profile deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Translator profile not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @translatorProfileService.isProfileOwner(#id, authentication.principal.username)")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        log.info("Deleting translator profile with ID: {}", id);
        
        translatorProfileService.delete(id);
        
        log.info("Translator profile deleted successfully with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Gets available translators for a specific theme.
     * Accessible by all authenticated users for finding translators.
     */
    @Operation(
            summary = "Get available translators by theme",
            description = "Retrieves available translators who can handle a specific theme."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Available translators retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/available/theme/{themeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<TranslatorProfileDto>> getAvailableTranslatorsByTheme(
            @PathVariable Long themeId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        
        log.debug("Retrieving available translators for theme ID: {}", themeId);
        
        Page<TranslatorProfileDto> translators = translatorProfileService.getAvailableByTheme(themeId, pageable);
        
        log.info("Found {} available translators for theme ID: {}", translators.getNumberOfElements(), themeId);
        return ResponseEntity.ok(translators);
    }
}