package com.morago.backend.exception;

public class InvalidJwtTokenException extends RuntimeException {
    public InvalidJwtTokenException() {
        super("Invalid Jwt Token");
    }
}
