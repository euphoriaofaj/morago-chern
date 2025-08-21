package com.morago.backend.service;

import com.morago.backend.config.utils.JWTUtils;
import com.morago.backend.dto.tokens.JWTRequest;
import com.morago.backend.dto.tokens.JWTResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

/**
 * Service implementation for handling user authentication operations.
 * Manages JWT token generation and user authentication flow.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    /**
     * Authenticates user credentials and generates JWT tokens.
     * 
     * @param authRequest containing username and password
     * @return JWTResponse with access and refresh tokens
     * @throws BadCredentialsException if authentication fails
     */
    @Override
    public JWTResponse createAuthToken(JWTRequest authRequest) {
        log.debug("Attempting authentication for user: {}", authRequest.getUsername());
        
        try {
            Authentication authentication = authenticateUser(authRequest);
            User authenticatedUser = (User) authentication.getPrincipal();
            
            String accessToken = generateAccessToken(authenticatedUser);
            String refreshToken = generateRefreshToken(authenticatedUser);
            
            storeRefreshToken(authenticatedUser.getUsername(), refreshToken);
            
            log.info("Successfully authenticated user: {}", authRequest.getUsername());
            return new JWTResponse(accessToken, refreshToken);
            
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for user: {}", authRequest.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }
    }
    
    /**
     * Authenticates user using Spring Security authentication manager.
     */
    private Authentication authenticateUser(JWTRequest authRequest) {
        return authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authRequest.getUsername(),
                authRequest.getPassword()
            )
        );
    }
    
    /**
     * Generates JWT access token for authenticated user.
     */
    private String generateAccessToken(User user) {
        return jwtUtils.generateAccessToken(user);
    }
    
    /**
     * Generates JWT refresh token for authenticated user.
     */
    private String generateRefreshToken(User user) {
        return jwtUtils.generateRefreshToken(user);
    }
    
    /**
     * Stores refresh token in database for future token refresh operations.
     */
    private void storeRefreshToken(String username, String refreshToken) {
        refreshTokenService.createRefreshToken(username, refreshToken);
    }
}