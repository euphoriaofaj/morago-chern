package com.morago.backend.exception;

public class ExpireJwtTokenException extends RuntimeException {
    public ExpireJwtTokenException() {
        super("Jwt Token has expired");
    }
}
