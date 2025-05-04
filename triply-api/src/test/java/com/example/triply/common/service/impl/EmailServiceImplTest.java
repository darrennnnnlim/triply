package com.example.triply.common.service.impl;

import com.example.triply.core.booking.entity.Booking;
import com.example.triply.core.auth.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EmailServiceImplTest {

    private JavaMailSender mailSender;
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        emailService = new EmailServiceImpl(mailSender);
    }

    @Test
    void testSendRegistrationEmail() {
        emailService.sendRegistrationEmail("user@example.com", "Darren");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertThat(message.getTo()).containsExactly("user@example.com");
        assertThat(message.getSubject()).isEqualTo("Welcome to Triply");
        assertThat(message.getText()).contains("Hello Darren");
    }

    @Test
    void testSendBanNotification_withReason() {
        emailService.sendBanNotification("banned@example.com", "Darren", "Violation of terms");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertThat(message.getSubject()).isEqualTo("Your Triply Account Status");
        assertThat(message.getText()).contains("Darren", "banned", "Violation of terms");
    }

    @Test
    void testSendBanNotification_withoutReason() {
        emailService.sendBanNotification("suspended@example.com", "Darren", "");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertThat(message.getText()).contains("suspended", "No reason provided");
    }

    @Test
    void testSendBookingConfirmationEmail() {
        User user = new User();
        user.setUsername("Darren");

        Booking booking = new Booking();
        booking.setId(101L);
        booking.setUser(user);
        booking.setStatus("CONFIRMED");
        booking.setFinalPrice(BigDecimal.valueOf(199.99));
        booking.setBookingTime(LocalDateTime.of(2025, 5, 4, 12, 0));

        emailService.sendBookingConfirmationEmail("booking@example.com", booking);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertThat(message.getSubject()).isEqualTo("Your Triply Booking Confirmation");
        assertThat(message.getText()).contains("Booking ID: 101", "Darren", "199.99", "CONFIRMED", "2025-05-04T12:00");
    }

    @Test
    void testSendEmail_generic() {
        emailService.sendEmail("anyone@example.com", "Subject Line", "Email body content");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertThat(message.getTo()).containsExactly("anyone@example.com");
        assertThat(message.getSubject()).isEqualTo("Subject Line");
        assertThat(message.getText()).isEqualTo("Email body content");
    }
}
