package com.morago.backend.controller;

import com.morago.backend.dto.tokens.JWTRequest;
import com.morago.backend.dto.tokens.JWTResponse;
import com.morago.backend.dto.tokens.RefreshTokenRequest;
import com.morago.backend.service.AuthService;
import com.morago.backend.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling authentication operations.
 * Provides endpoints for login, token refresh, and logout functionality.
 */
@Tag(name = "Authentication", description = "User authentication and token management endpoints")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class MainController {
    
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    /**
     * Authenticates user credentials and returns JWT tokens.
     * 
     * @param authRequest containing username and password
     * @return ResponseEntity with JWT tokens or error response
     */
    @Operation(
            summary = "Authenticate user and generate tokens",
            description = "Validates user credentials and returns JWT access token and refresh token for subsequent API calls.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "User credentials for authentication",
                    content = @Content(
                            schema = @Schema(implementation = JWTRequest.class),
                            examples = @ExampleObject(
                                    name = "Login Example",
                                    summary = "Sample login request",
                                    value = "{\"username\":\"01012345673\",\"password\":\"123456\"}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Authentication successful",
                    content = @Content(
                            schema = @Schema(implementation = JWTResponse.class),
                            examples = @ExampleObject(
                                    name = "Success Response",
                                    value = "{\"accessToken\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\"refreshToken\":\"dGhpc2lzYXJlZnJlc2h0b2tlbg==\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401", 
                    description = "Authentication failed - Invalid credentials",
                    content = @Content(
                            schema = @Schema(type = "object"),
                            examples = @ExampleObject(
                                    name = "Error Response",
                                    value = "{\"error\":\"Invalid username or password\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400", 
                    description = "Bad request - Invalid input format"
            )
    })
    @PostMapping("/login")
    public ResponseEntity<JWTResponse> login(@Valid @RequestBody JWTRequest authRequest) {
        log.info("Login attempt for user: {}", authRequest.getUsername());
        
        try {
            JWTResponse response = authService.createAuthToken(authRequest);
            log.info("Login successful for user: {}", authRequest.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.warn("Login failed for user: {} - {}", authRequest.getUsername(), e.getMessage());
            throw e; // Let global exception handler manage the response
        }
    }

    /**
     * Refreshes access token using a valid refresh token.
     * 
     * @param request containing the refresh token
     * @return ResponseEntity with new JWT tokens or error response
     */
    @Operation(
            summary = "Refresh access token",
            description = "Generates a new access token and refresh token using a valid refresh token. The old refresh token will be invalidated.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Valid refresh token for generating new tokens",
                    content = @Content(
                            schema = @Schema(implementation = RefreshTokenRequest.class),
                            examples = @ExampleObject(
                                    name = "Refresh Token Example",
                                    summary = "Sample refresh token request",
                                    value = "{\"refreshToken\":\"dGhpc2lzYXJlZnJlc2h0b2tlbg==\"}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Token refresh successful",
                    content = @Content(schema = @Schema(implementation = JWTResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401", 
                    description = "Token refresh failed - Invalid or expired refresh token",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "{\"accessToken\":\"\",\"refreshToken\":\"\"}"
                            )
                    )
            )
    })
    @PostMapping("/refresh_token")
    public ResponseEntity<JWTResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.debug("Token refresh request received");
        
        try {
            JWTResponse jwtResponse = refreshTokenService.refreshToken(request.getRefreshToken());
            log.info("Token refresh successful");
            return ResponseEntity.ok(jwtResponse);
            
        } catch (RuntimeException e) {
            log.warn("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new JWTResponse("", ""));
        }
    }

    /**
     * Logs out user by invalidating their refresh token.
     * 
     * @param request containing the refresh token to invalidate
     * @return ResponseEntity with success or error message
     */
    @Operation(
            summary = "Logout user",
            description = "Invalidates the user's refresh token, effectively logging them out. Requires valid authentication.",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Refresh token to invalidate",
                    content = @Content(
                            schema = @Schema(implementation = RefreshTokenRequest.class),
                            examples = @ExampleObject(
                                    name = "Logout Example",
                                    summary = "Sample logout request",
                                    value = "{\"refreshToken\":\"dGhpc2lzYXJlZnJlc2h0b2tlbg==\"}"
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Logout successful",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = "\"Logged out successfully\""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400", 
                    description = "Bad request - Invalid refresh token"
            ),
            @ApiResponse(
                    responseCode = "401", 
                    description = "Unauthorized - Invalid or missing authentication"
            )
    })
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest request) {
        log.debug("Logout request received");
        
        try {
            refreshTokenService.logoutUserByRefreshToken(request.getRefreshToken());
            log.info("User logout successful");
            return ResponseEntity.ok("Logged out successfully");
            
        } catch (RuntimeException e) {
            log.warn("Logout failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Logout failed: " + e.getMessage());
        }
    }
}