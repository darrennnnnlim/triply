package com.example.triply.common.service;

import com.example.triply.core.booking.entity.Booking;

public interface EmailService {
    void sendRegistrationEmail(String toEmail, String username);
    void sendBanNotification(String toEmail, String username, String reason);
    void sendBookingConfirmationEmail(String toEmail, Booking booking);
}