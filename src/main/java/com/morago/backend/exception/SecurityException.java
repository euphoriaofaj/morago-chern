package com.morago.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class SecurityException extends RuntimeException {
    
    public SecurityException(String message) {
        super(message);
    }
    
    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static class InsufficientPermissionsException extends SecurityException {
        public InsufficientPermissionsException(String action) {
            super("Insufficient permissions to perform action: " + action);
        }
    }
    
    public static class UnauthorizedAccessException extends SecurityException {
        public UnauthorizedAccessException(String resource) {
            super("Unauthorized access to resource: " + resource);
        }
    }
    
    public static class InvalidRoleException extends SecurityException {
        public InvalidRoleException(String role) {
            super("Invalid or insufficient role: " + role);
        }
    }
}