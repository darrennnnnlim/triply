package com.example.triply.common.service;

public interface EmailService {
    void sendRegistrationEmail(String toEmail, String username);
    void sendBanNotification(String toEmail, String username, String reason);
}