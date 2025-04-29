package com.example.triply.common.exception;

public class SameNewPasswordException extends RuntimeException {
    public SameNewPasswordException() {
        super("New password cannot be the same as the current password.");
    }
}