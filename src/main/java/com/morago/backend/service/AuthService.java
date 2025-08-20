package com.morago.backend.service;

import com.morago.backend.dto.tokens.JWTRequest;
import com.morago.backend.dto.tokens.JWTResponse;

public interface AuthService {
    JWTResponse createAuthToken(JWTRequest authRequest);
}
