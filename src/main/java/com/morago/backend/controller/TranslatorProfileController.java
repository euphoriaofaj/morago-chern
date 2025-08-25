package com.morago.backend.controller;

import com.morago.backend.dto.request.TranslatorProfileCreateRequest;
import com.morago.backend.dto.request.TranslatorProfileUpdateRequest;
import com.morago.backend.dto.response.TranslatorProfileResponse;
import com.morago.backend.dto.response.TranslatorProfileSummaryResponse;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
     * Only accessible by ADMIN or users with TRANSLATOR role creating their own profile.
     */
    @Operation(
            summary = "Create translator profile",
            description = "Creates a new translator profile. Only admins or users with TRANSLATOR role can create profiles."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Translator profile created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data or profile already exists"),
            @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('TRANSLATOR') and #request.userId == authentication.principal.id)")
    public ResponseEntity<TranslatorProfileResponse> createProfile(
            @Valid @RequestBody TranslatorProfileCreateRequest request,
            Authentication authentication) {
        
        log.info("Creating translator profile for user ID: {} by user: {}", 
                request.getUserId(), authentication.getName());
        
        TranslatorProfileResponse created = translatorProfileService.create(request);
        
        log.info("Translator profile created successfully with ID: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Retrieves all translator profiles with pagination and filtering.
     * Accessible by all authenticated users for browsing translators.
     */
    @Operation(
            summary = "Get all translator profiles",
            description = "Retrieves paginated list of translator profiles with optional filtering by availability, language, theme, and search."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Translator profiles retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<TranslatorProfileSummaryResponse>> getAllProfiles(
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
            @RequestParam(required = false) String levelOfKorean,
            @Parameter(description = "Search by name, email, or Korean level") 
            @RequestParam(required = false) String search) {
        
        log.debug("Retrieving translator profiles with filters - available: {}, online: {}, language: {}, theme: {}, search: '{}'", 
                isAvailable, isOnline, languageId, themeId, search);
        
        Page<TranslatorProfileSummaryResponse> profiles = translatorProfileService.getAllWithFilters(
                pageable, isAvailable, isOnline, languageId, themeId, levelOfKorean, search);
        
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
            description = "Retrieves a specific translator profile by its ID with detailed information."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Translator profile found"),
            @ApiResponse(responseCode = "404", description = "Translator profile not found"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TranslatorProfileResponse> getProfileById(@PathVariable Long id) {
        log.debug("Retrieving translator profile with ID: {}", id);
        
        TranslatorProfileResponse profile = translatorProfileService.getById(id);
        
        log.debug("Translator profile found: {}", profile.getEmail());
        return ResponseEntity.ok(profile);
    }

    /**
     * Retrieves translator profile by user ID.
     * Accessible by ADMIN or the profile owner.
     */
    @Operation(
            summary = "Get translator profile by user ID",
            description = "Retrieves translator profile associated with a specific user ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Translator profile found"),
            @ApiResponse(responseCode = "404", description = "Translator profile not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<TranslatorProfileResponse> getProfileByUserId(@PathVariable Long userId) {
        log.debug("Retrieving translator profile for user ID: {}", userId);
        
        TranslatorProfileResponse profile = translatorProfileService.getByUserId(userId);
        
        log.debug("Translator profile found for user: {}", profile.getUsername());
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
    public ResponseEntity<TranslatorProfileResponse> updateProfile(
            @PathVariable Long id, 
            @Valid @RequestBody TranslatorProfileUpdateRequest request,
            Authentication authentication) {
        
        log.info("Updating translator profile with ID: {} by user: {}", id, authentication.getName());
        
        TranslatorProfileResponse updated = translatorProfileService.update(id, request);
        
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
            @ApiResponse(responseCode = "400", description = "Invalid availability status"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Translator profile not found")
    })
    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasRole('ADMIN') or @translatorProfileService.isProfileOwner(#id, authentication.principal.username)")
    public ResponseEntity<TranslatorProfileResponse> updateAvailability(
            @PathVariable Long id,
            @Parameter(description = "New availability status", required = true)
            @RequestParam Boolean isAvailable,
            Authentication authentication) {
        
        log.info("Updating availability for translator profile ID: {} to {} by user: {}", 
                id, isAvailable, authentication.getName());
        
        TranslatorProfileResponse updated = translatorProfileService.updateAvailability(id, isAvailable);
        
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
            @ApiResponse(responseCode = "400", description = "Invalid online status"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Translator profile not found")
    })
    @PatchMapping("/{id}/online-status")
    @PreAuthorize("hasRole('ADMIN') or @translatorProfileService.isProfileOwner(#id, authentication.principal.username)")
    public ResponseEntity<TranslatorProfileResponse> updateOnlineStatus(
            @PathVariable Long id,
            @Parameter(description = "New online status", required = true)
            @RequestParam Boolean isOnline,
            Authentication authentication) {
        
        log.info("Updating online status for translator profile ID: {} to {} by user: {}", 
                id, isOnline, authentication.getName());
        
        TranslatorProfileResponse updated = translatorProfileService.updateOnlineStatus(id, isOnline);
        
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
    public ResponseEntity<Void> deleteProfile(
            @PathVariable Long id,
            Authentication authentication) {
        
        log.info("Deleting translator profile with ID: {} by user: {}", id, authentication.getName());
        
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
            description = "Retrieves available translators who can handle a specific theme and are currently online."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Available translators retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/available/theme/{themeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<TranslatorProfileSummaryResponse>> getAvailableTranslatorsByTheme(
            @PathVariable Long themeId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        
        log.debug("Retrieving available translators for theme ID: {}", themeId);
        
        Page<TranslatorProfileSummaryResponse> translators = translatorProfileService.getAvailableByTheme(themeId, pageable);
        
        log.info("Found {} available translators for theme ID: {}", translators.getNumberOfElements(), themeId);
        return ResponseEntity.ok(translators);
    }

    /**
     * Gets translator statistics.
     * Only accessible by ADMIN.
     */
    @Operation(
            summary = "Get translator statistics",
            description = "Retrieves overall statistics about translators in the system. Only accessible by admins."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied - admin role required")
    })
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getTranslatorStatistics() {
        log.debug("Retrieving translator statistics");
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalTranslators", translatorProfileService.getTotalTranslators());
        statistics.put("onlineTranslators", translatorProfileService.getOnlineTranslators());
        statistics.put("availableTranslators", translatorProfileService.getAvailableTranslators());
        
        log.info("Translator statistics retrieved successfully");
        return ResponseEntity.ok(statistics);
    }
}