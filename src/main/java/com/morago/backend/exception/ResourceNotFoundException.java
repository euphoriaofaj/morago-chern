package com.morago.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s with id '%d' not found", resourceName, id));
    }

    public ResourceNotFoundException(String resourceName, String fieldValue) {
        super(String.format("%s with value '%s' not found", resourceName, fieldValue));
    }
}

