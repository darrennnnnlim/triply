package com.example.triply.common.service;

public interface EmailService {
    void sendRegistrationEmail(String to, String subject, String content);
}