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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "User authentication and token management")
@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class MainController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @Operation(
            summary = "Log in with username and password",
            description = "Authenticates the user and returns a JWT access token and refresh token.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = JWTRequest.class),
                            examples = @ExampleObject(
                                    name = "Example",
                                    value = "{\"username\":\"admin@example.com\",\"password\":\"P@ssw0rd\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful authentication",
                            content = @Content(schema = @Schema(implementation = JWTResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid username or password")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<JWTResponse> login(@RequestBody JWTRequest authRequest) {
        JWTResponse response = authService.createAuthToken(authRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Refresh access token",
            description = "Generates a new access token using a valid refresh token.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RefreshTokenRequest.class),
                            examples = @ExampleObject(
                                    name = "Example",
                                    value = "{\"refreshToken\":\"<your_refresh_token>\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "New access token generated",
                            content = @Content(schema = @Schema(implementation = JWTResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
            }
    )
    @PostMapping("/refresh_token")
    public ResponseEntity<JWTResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            JWTResponse jwtResponse = refreshTokenService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(jwtResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new JWTResponse("", ""));
        }
    }

    @Operation(
            summary = "Log out",
            description = "Invalidates the provided refresh token. Requires a valid Bearer token in the Authorization header.",
            security = @SecurityRequirement(name = "bearerAuth"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RefreshTokenRequest.class),
                            examples = @ExampleObject(
                                    name = "Example",
                                    value = "{\"refreshToken\":\"<user_refresh_token>\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully logged out"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequest request) {
        try {
            refreshTokenService.logoutUserByRefreshToken(request.getRefreshToken());
            return ResponseEntity.ok("Logged out successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
