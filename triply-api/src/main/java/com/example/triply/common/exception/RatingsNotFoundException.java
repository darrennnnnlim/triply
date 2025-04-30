package com.example.triply.common.exception;

public class RatingsNotFoundException extends RuntimeException {
    public RatingsNotFoundException(String message) {
        super(message);
    }

    public RatingsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
