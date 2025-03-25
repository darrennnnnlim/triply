package com.example.triply.common.exception;

public class UsernameAlreadyExistException extends RuntimeException {
    public UsernameAlreadyExistException() {
        super("Username already exists.");
    }
}
