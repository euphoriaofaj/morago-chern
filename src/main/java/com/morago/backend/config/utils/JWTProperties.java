package com.morago.backend.config.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JWTProperties {
    private String accessSecret;
    private String refreshSecret;
    private long accessExpirationMs;
    private long refreshExpirationMs;
}
