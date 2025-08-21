package com.morago.backend.service;

import com.morago.backend.config.utils.JWTProperties;
import com.morago.backend.config.utils.JWTUtils;
import com.morago.backend.dto.tokens.JWTResponse;
import com.morago.backend.entity.RefreshToken;
import com.morago.backend.entity.User;
import com.morago.backend.exception.ExpireJwtTokenException;
import com.morago.backend.exception.RefreshTokenNotFoundException;
import com.morago.backend.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service implementation for managing JWT refresh tokens.
 * Handles token creation, validation, refresh operations, and cleanup.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final JWTProperties jwtProperties;
    private final JWTUtils jwtUtils;

    /**
     * Creates and stores a new refresh token for the specified user.
     * 
     * @param username the username to associate with the token
     * @param jwtTokenString the JWT token string to store
     */
    @Override
    public void createRefreshToken(String username, String jwtTokenString) {
        log.debug("Creating refresh token for user: {}", username);
        
        User user = userService.findByUsernameOrThrow(username);
        
        RefreshToken token = buildRefreshToken(jwtTokenString, user);
        refreshTokenRepository.save(token);
        
        log.info("Refresh token created successfully for user: {}", username);
    }

    /**
     * Finds a refresh token by its token string.
     * 
     * @param token the token string to search for
     * @return Optional containing the RefreshToken if found
     */
    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Checks if a refresh token has expired.
     * 
     * @param token the refresh token to check
     * @return true if the token has expired, false otherwise
     */
    @Override
    public boolean isRefreshTokenExpired(RefreshToken token) {
        boolean isExpired = token.getExpirationTime().isBefore(LocalDateTime.now());
        
        if (isExpired) {
            log.warn("Refresh token has expired for user: {}", token.getUser().getUsername());
        }
        
        return isExpired;
    }

    /**
     * Deletes all refresh tokens associated with a user.
     * 
     * @param user the user whose tokens should be deleted
     */
    @Override
    @Transactional
    public void deleteByUser(User user) {
        log.debug("Deleting all refresh tokens for user: {}", user.getUsername());
        refreshTokenRepository.deleteByUser(user);
    }

    /**
     * Deletes a specific refresh token by its token string.
     * 
     * @param token the token string to delete
     */
    @Override
    public void deleteByToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshToken -> {
                    log.debug("Deleting refresh token for user: {}", refreshToken.getUser().getUsername());
                    refreshTokenRepository.delete(refreshToken);
                });
    }

    /**
     * Refreshes an access token using a valid refresh token.
     * 
     * @param requestRefreshToken the refresh token to use for generating new tokens
     * @return JWTResponse containing new access and refresh tokens
     * @throws RefreshTokenNotFoundException if token is not found
     * @throws ExpireJwtTokenException if token has expired
     */
    @Override
    @Transactional
    public JWTResponse refreshToken(String requestRefreshToken) {
        log.debug("Processing token refresh request");
        
        RefreshToken refreshToken = getValidTokenOrThrow(requestRefreshToken);
        User user = refreshToken.getUser();
        
        // Generate new tokens
        String newAccessToken = jwtUtils.generateAccessToken(user);
        String newRefreshToken = jwtUtils.generateRefreshToken(user);
        
        // Clean up old token and create new one
        deleteByToken(requestRefreshToken);
        createRefreshToken(user.getUsername(), newRefreshToken);
        
        log.info("Token refresh successful for user: {}", user.getUsername());
        return new JWTResponse(newAccessToken, newRefreshToken);
    }

    /**
     * Logs out a user by invalidating their refresh token.
     * 
     * @param refreshTokenStr the refresh token to invalidate
     * @throws RefreshTokenNotFoundException if token is not found
     */
    @Override
    public void logoutUserByRefreshToken(String refreshTokenStr) {
        log.debug("Processing logout request");
        
        RefreshToken refreshToken = findByTokenOrThrow(refreshTokenStr);
        String username = refreshToken.getUser().getUsername();
        
        deleteByUser(refreshToken.getUser());
        
        log.info("User logged out successfully: {}", username);
    }
    
    /**
     * Builds a RefreshToken entity with proper expiration time.
     */
    private RefreshToken buildRefreshToken(String jwtTokenString, User user) {
        LocalDateTime now = LocalDateTime.now();
        Duration refreshDuration = Duration.ofMillis(jwtProperties.getRefreshExpirationMs());
        
        return RefreshToken.builder()
                .token(jwtTokenString)
                .user(user)
                .createdAt(now)
                .expirationTime(now.plus(refreshDuration))
                .build();
    }
}