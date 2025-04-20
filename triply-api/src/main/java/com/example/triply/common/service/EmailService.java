package com.example.triply.common.service;

import com.example.triply.core.booking.entity.Booking;

public interface EmailService {
    void sendRegistrationEmail(String toEmail, String username);
    void sendBanNotification(String toEmail, String username, String reason);
    void sendBookingConfirmationEmail(String toEmail, Booking booking);
    void sendEmail(String toEmail, String subject, String body); // Added generic email method
}