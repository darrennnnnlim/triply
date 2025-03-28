package com.example.triply.common.exception;
public class UsernameAlreadyExistException extends RuntimeException {
    public UsernameAlreadyExistException() {
        super("Username already exists.");
    }

    public UsernameAlreadyExistException(String message) {
        super(message);
    }
}
