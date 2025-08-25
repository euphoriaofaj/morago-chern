package com.morago.backend.service;

import com.morago.backend.dto.request.TranslatorProfileCreateRequest;
import com.morago.backend.dto.request.TranslatorProfileUpdateRequest;
import com.morago.backend.dto.response.TranslatorProfileResponse;
import com.morago.backend.dto.response.TranslatorProfileSummaryResponse;
import com.morago.backend.entity.Language;
import com.morago.backend.entity.Theme;
import com.morago.backend.entity.TranslatorProfile;
import com.morago.backend.entity.User;
import com.morago.backend.entity.enumFiles.Roles;
import com.morago.backend.exception.SecurityException;
import com.morago.backend.exception.TranslatorProfileException;
import com.morago.backend.exception.ResourceNotFoundException;
import com.morago.backend.mapper.TranslatorProfileMapper;
import com.morago.backend.repository.LanguageRepository;
import com.morago.backend.repository.RatingRepository;
import com.morago.backend.repository.CallRepository;
import com.morago.backend.repository.ThemeRepository;
import com.morago.backend.repository.TranslatorProfileRepository;
import com.morago.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TranslatorProfileServiceImpl implements TranslatorProfileService {

    private final TranslatorProfileRepository profileRepo;
    private final UserRepository userRepo;
    private final LanguageRepository languageRepo;
    private final ThemeRepository themeRepo;
    private final RatingRepository ratingRepo;
    private final CallRepository callRepo;
    private final TranslatorProfileMapper mapper;

    @Override
    public TranslatorProfileResponse create(TranslatorProfileCreateRequest request) {
        log.debug("Creating translator profile for user ID: {}", request.getUserId());
        
        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate user has TRANSLATOR role
        boolean hasTranslatorRole = user.getRoles().stream()
                .anyMatch(role -> role.getName() == Roles.ROLE_TRANSLATOR);
        if (!hasTranslatorRole) {
            throw new SecurityException.InvalidRoleException("User must have TRANSLATOR role to create translator profile");
        }

        // Check if profile already exists for this user
        if (profileRepo.existsByUserId(request.getUserId())) {
            throw new TranslatorProfileException.ProfileAlreadyExistsException(request.getUserId());
        }

        Set<Language> languages = (request.getLanguageIds() != null)
                ? request.getLanguageIds().stream()
                .map(id -> languageRepo.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Language not found: " + id)))
                .collect(Collectors.toSet())
                : Set.of();

        Set<Theme> themes = (request.getThemeIds() != null)
                ? request.getThemeIds().stream()
                .map(id -> themeRepo.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Theme not found: " + id)))
                .collect(Collectors.toSet())
                : Set.of();

        TranslatorProfile profile = TranslatorProfile.builder()
                .dateOfBirth(request.getDateOfBirth())
                .email(request.getEmail())
                .isAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : false)
                .isOnline(request.getIsOnline() != null ? request.getIsOnline() : false)
                .levelOfKorean(request.getLevelOfKorean())
                .user(user)
                .languages(languages)
                .themes(themes)
                .build();

        TranslatorProfile saved = profileRepo.save(profile);
        log.info("Translator profile created successfully with ID: {}", saved.getId());
        
        TranslatorProfileResponse response = mapper.toResponse(saved);
        enrichWithStatistics(response, saved);
        return response;
    }

    @Override
    public TranslatorProfileResponse getById(Long id) {
        TranslatorProfile profile = profileRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        
        TranslatorProfileResponse response = mapper.toResponse(profile);
        enrichWithStatistics(response, profile);
        return response;
    }
    
    @Override
    public TranslatorProfileResponse getByUserId(Long userId) {
        TranslatorProfile profile = profileRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Translator profile not found for user ID: " + userId));
        
        TranslatorProfileResponse response = mapper.toResponse(profile);
        enrichWithStatistics(response, profile);
        return response;
    }

    @Override
    public Page<TranslatorProfileSummaryResponse> getAllWithFilters(
            Pageable pageable,
            Boolean isAvailable,
            Boolean isOnline,
            Long languageId,
            Long themeId,
            String levelOfKorean,
            String search) {
        
        log.debug("Fetching translator profiles with filters - available: {}, online: {}, language: {}, theme: {}, search: {}", 
                isAvailable, isOnline, languageId, themeId, search);
        
        Page<TranslatorProfile> profiles = profileRepo.findWithFilters(
                isAvailable, isOnline, languageId, themeId, levelOfKorean, search, pageable);
        
        return profiles.map(profile -> {
            TranslatorProfileSummaryResponse response = mapper.toSummaryResponse(profile);
            enrichSummaryWithStatistics(response, profile);
            return response;
        });
    }

    @Override
    public Page<TranslatorProfileSummaryResponse> getAvailableByTheme(Long themeId, Pageable pageable) {
        log.debug("Fetching available translators for theme ID: {}", themeId);
        
        Page<TranslatorProfile> profiles = profileRepo.findAvailableByTheme(themeId, pageable);
        return profiles.map(profile -> {
            TranslatorProfileSummaryResponse response = mapper.toSummaryResponse(profile);
            enrichSummaryWithStatistics(response, profile);
            return response;
        });
    }
    
    @Override
    public TranslatorProfileResponse update(Long id, TranslatorProfileUpdateRequest request) {
        log.debug("Updating translator profile with ID: {}", id);
        
        TranslatorProfile profile = profileRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        if (request.getDateOfBirth() != null) profile.setDateOfBirth(request.getDateOfBirth());
        if (request.getEmail() != null) profile.setEmail(request.getEmail());
        if (request.getIsAvailable() != null) profile.setIsAvailable(request.getIsAvailable());
        if (request.getIsOnline() != null) profile.setIsOnline(request.getIsOnline());
        if (request.getLevelOfKorean() != null) profile.setLevelOfKorean(request.getLevelOfKorean());

        if (request.getLanguageIds() != null) {
            Set<Language> languages = request.getLanguageIds().stream()
                    .map(idL -> languageRepo.findById(idL)
                            .orElseThrow(() -> new ResourceNotFoundException("Language not found: " + idL)))
                    .collect(Collectors.toSet());
            profile.setLanguages(languages);
        }

        if (request.getThemeIds() != null) {
            Set<Theme> themes = request.getThemeIds().stream()
                    .map(idT -> themeRepo.findById(idT)
                            .orElseThrow(() -> new ResourceNotFoundException("Theme not found: " + idT)))
                    .collect(Collectors.toSet());
            profile.setThemes(themes);
        }

        TranslatorProfile updated = profileRepo.save(profile);
        log.info("Translator profile updated successfully with ID: {}", updated.getId());
        
        TranslatorProfileResponse response = mapper.toResponse(updated);
        enrichWithStatistics(response, updated);
        return response;
    }

    @Override
    public TranslatorProfileResponse updateAvailability(Long id, Boolean isAvailable) {
        log.debug("Updating availability for translator profile ID: {} to {}", id, isAvailable);
        
        TranslatorProfile profile = profileRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        
        if (isAvailable == null) {
            throw new TranslatorProfileException.InvalidAvailabilityException("Availability status cannot be null");
        }
        
        profile.setIsAvailable(isAvailable);
        TranslatorProfile updated = profileRepo.save(profile);
        
        log.info("Availability updated successfully for translator profile ID: {}", id);
        
        TranslatorProfileResponse response = mapper.toResponse(updated);
        enrichWithStatistics(response, updated);
        return response;
    }

    @Override
    public TranslatorProfileResponse updateOnlineStatus(Long id, Boolean isOnline) {
        log.debug("Updating online status for translator profile ID: {} to {}", id, isOnline);
        
        TranslatorProfile profile = profileRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        
        if (isOnline == null) {
            throw new TranslatorProfileException.InvalidAvailabilityException("Online status cannot be null");
        }
        
        profile.setIsOnline(isOnline);
        TranslatorProfile updated = profileRepo.save(profile);
        
        log.info("Online status updated successfully for translator profile ID: {}", id);
        
        TranslatorProfileResponse response = mapper.toResponse(updated);
        enrichWithStatistics(response, updated);
        return response;
    }

    @Override
    public boolean isProfileOwner(Long profileId, String username) {
        return profileRepo.findById(profileId)
                .map(profile -> profile.getUser().getUsername().equals(username))
                .orElse(false);
    }
    
    @Override
    public boolean canAccessProfile(Long profileId, String username, String role) {
        if ("ROLE_ADMIN".equals(role)) {
            return true;
        }
        return isProfileOwner(profileId, username);
    }

    @Override
    public void delete(Long id) {
        log.debug("Deleting translator profile with ID: {}", id);
        
        TranslatorProfile profile = profileRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        profileRepo.delete(profile);
        
        log.info("Translator profile deleted successfully with ID: {}", id);
    }
    
    @Override
    public Long getTotalTranslators() {
        return profileRepo.count();
    }
    
    @Override
    public Long getOnlineTranslators() {
        return profileRepo.countByIsOnlineTrue();
    }
    
    @Override
    public Long getAvailableTranslators() {
        return profileRepo.countByIsAvailableTrue();
    }
    
    private void enrichWithStatistics(TranslatorProfileResponse response, TranslatorProfile profile) {
        // Calculate average rating
        Double avgRating = ratingRepo.findAverageRatingByTranslatorId(profile.getId());
        response.setAverageRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);
        
        // Count total ratings
        Long totalRatings = ratingRepo.countByTranslatorId(profile.getId());
        response.setTotalRatings(totalRatings);
        
        // Count total completed calls
        Long totalCalls = callRepo.countCompletedCallsByTranslatorId(profile.getId());
        response.setTotalCalls(totalCalls);
    }
    
    private void enrichSummaryWithStatistics(TranslatorProfileSummaryResponse response, TranslatorProfile profile) {
        // Calculate average rating
        Double avgRating = ratingRepo.findAverageRatingByTranslatorId(profile.getId());
        response.setAverageRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);
        
        // Count total completed calls
        Long totalCalls = callRepo.countCompletedCallsByTranslatorId(profile.getId());
        response.setTotalCalls(totalCalls);
        
        // Check if translator is currently in a call
        Boolean inCall = callRepo.existsActiveCallByTranslatorId(profile.getId());
        response.setInCall(inCall);
    }
}

