package com.morago.backend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import jakarta.annotation.PostConstruct;

/**
 * Configuration class for managing application profiles.
 * Provides profile-specific initialization and logging.
 */
@Slf4j
@Configuration
public class ProfileConfig {

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Value("${app.environment:unknown}")
    private String environment;

    /**
     * Development profile configuration.
     * Enables additional debugging and development features.
     */
    @Configuration
    @Profile("dev")
    static class DevelopmentConfig {
        
        @PostConstruct
        public void init() {
            log.info("=".repeat(50));
            log.info("🚀 MORAGO Backend - DEVELOPMENT MODE");
            log.info("=".repeat(50));
            log.info("📊 Debug logging enabled");
            log.info("🔧 Development tools active");
            log.info("📖 Swagger UI available at: /swagger");
            log.info("🔍 API docs available at: /v3/api-docs");
            log.info("=".repeat(50));
        }
    }

    /**
     * Production profile configuration.
     * Optimizes settings for production environment.
     */
    @Configuration
    @Profile("prod")
    static class ProductionConfig {
        
        @PostConstruct
        public void init() {
            log.info("=".repeat(50));
            log.info("🏭 MORAGO Backend - PRODUCTION MODE");
            log.info("=".repeat(50));
            log.info("🔒 Security optimizations enabled");
            log.info("📊 Performance monitoring active");
            log.info("🚫 Debug features disabled");
            log.info("=".repeat(50));
        }
    }

    /**
     * Test profile configuration.
     * Configures settings for testing environment.
     */
    @Configuration
    @Profile("test")
    static class TestConfig {
        
        @PostConstruct
        public void init() {
            log.info("=".repeat(50));
            log.info("🧪 MORAGO Backend - TEST MODE");
            log.info("=".repeat(50));
            log.info("🔬 Test configurations loaded");
            log.info("📝 Test database active");
            log.info("=".repeat(50));
        }
    }

    @PostConstruct
    public void logActiveProfile() {
        log.info("Active Profile: {}", activeProfile);
        log.info("Environment: {}", environment);
    }
}