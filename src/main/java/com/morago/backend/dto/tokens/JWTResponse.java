package com.morago.backend.dto.tokens;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Authentication response containing access and refresh tokens")
public class JWTResponse {

    @Schema(description = "JWT access token used for authorization", example = "eyJhbGciOiJIUzI1NiIsInR5cCI...")
    private String accessToken;

    @Schema(description = "JWT refresh token used to obtain new access tokens", example = "dGhpc2lzYXJlZnJlc2h0b2tlbg==")
    private String refreshToken;
}

