package com.morago.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TranslatorProfileException extends RuntimeException {
    
    public TranslatorProfileException(String message) {
        super(message);
    }
    
    public TranslatorProfileException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static class ProfileAlreadyExistsException extends TranslatorProfileException {
        public ProfileAlreadyExistsException(Long userId) {
            super("Translator profile already exists for user ID: " + userId);
        }
    }
    
    public static class ProfileNotOwnedException extends TranslatorProfileException {
        public ProfileNotOwnedException() {
            super("You can only access your own translator profile");
        }
    }
    
    public static class InvalidAvailabilityException extends TranslatorProfileException {
        public InvalidAvailabilityException(String message) {
            super("Invalid availability status: " + message);
        }
    }
    
    public static class ProfileIncompleteException extends TranslatorProfileException {
        public ProfileIncompleteException(String missingField) {
            super("Profile is incomplete. Missing required field: " + missingField);
        }
    }
}