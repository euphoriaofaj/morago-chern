package com.morago.backend.service;

import com.morago.backend.dto.tokens.JWTResponse;
import com.morago.backend.entity.RefreshToken;
import com.morago.backend.entity.User;
import com.morago.backend.exception.ExpireJwtTokenException;
import com.morago.backend.exception.RefreshTokenNotFoundException;
import java.util.Optional;

public interface RefreshTokenService {
    void createRefreshToken(String username, String jwtTokenString);
    Optional<RefreshToken> findByToken(String token);
    default RefreshToken findByTokenOrThrow(String token) {
        return findByToken(token).orElseThrow(RefreshTokenNotFoundException::new);
    }
    default RefreshToken getValidTokenOrThrow(String token) {
        RefreshToken rt = findByTokenOrThrow(token);
        if (isRefreshTokenExpired(rt)) {
            deleteByToken(token);
            throw new ExpireJwtTokenException();
        }
        return rt;
    }
    boolean isRefreshTokenExpired(RefreshToken token);
    void deleteByUser(User user);
    void deleteByToken(String token);
    JWTResponse refreshToken(String requestRefreshToken);
    void logoutUserByRefreshToken(String refreshTokenStr);
}
