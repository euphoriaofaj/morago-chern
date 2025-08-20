package com.morago.backend.config.utils;

import com.morago.backend.entity.enumFiles.TokenType;
import com.morago.backend.exception.ExpireJwtTokenException;
import com.morago.backend.exception.InvalidJwtTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JWTUtils {
    private final JWTProperties jwtProperties;
    public JWTUtils(JWTProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    private Key getAccessSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getAccessSecret().getBytes());
    }

    private Key getRefreshSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getRefreshSecret().getBytes());
    }

    private Key getSigningKey(TokenType type) {
        return switch (type) {
            case ACCESS -> getAccessSigningKey();
            case REFRESH -> getRefreshSigningKey();
        };
    }

    public String generateAccessToken(UserDetails userDetails) {
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessExpirationMs()))
                .signWith(getAccessSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshExpirationMs()))
                .signWith(getRefreshSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token, TokenType type) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(type))
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            throw new ExpireJwtTokenException();
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidJwtTokenException();
        }
    }

    @SuppressWarnings("unchecked")
    public Set<String> getRolesFromToken(String token, TokenType type) {
        try {
            return new HashSet<>((List<String>) Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(type))
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("roles"));
        } catch (ExpiredJwtException e) {
            throw new ExpireJwtTokenException();
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidJwtTokenException();
        }
    }

    public void validateToken(String token, TokenType type) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(type))
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new ExpireJwtTokenException();
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidJwtTokenException();
        }
    }
}
