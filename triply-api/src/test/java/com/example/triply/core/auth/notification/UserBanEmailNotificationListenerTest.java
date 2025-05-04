package com.example.triply.core.auth.notification;

import com.example.triply.common.service.EmailService;
import com.example.triply.core.auth.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class UserBanEmailNotificationListenerTest {

    private EmailService emailService;
    private UserBanEmailNotificationListener listener;

    @BeforeEach
    void setUp() {
        //Disable logger for unit tests
        org.slf4j.Logger root = org.slf4j.LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        if (root instanceof ch.qos.logback.classic.Logger logger) {
            logger.setLevel(ch.qos.logback.classic.Level.OFF);
        }

        emailService = mock(EmailService.class);
        listener = new UserBanEmailNotificationListener(emailService);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void shouldSendBanEmailForValidUser() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setEmail("alice@example.com");

        UserBanWriteEvent event = mock(UserBanWriteEvent.class);
        when(event.getUser()).thenReturn(user);
        when(event.getReason()).thenReturn("Violation of terms");

        // Act
        listener.onUserBan(event);

        // Assert
        verify(emailService).sendBanNotification(
                eq("alice@example.com"),
                eq("alice"),
                eq("Violation of terms")
        );
    }

    @Test
    void shouldNotSendEmailIfUserIsNull() {
        // Arrange
        UserBanWriteEvent event = mock(UserBanWriteEvent.class);
        when(event.getUser()).thenReturn(null);

        // Act
        listener.onUserBan(event);

        // Assert
        verifyNoInteractions(emailService);
    }

    @Test
    void shouldNotSendEmailIfEmailIsNull() {
        // Arrange
        User user = new User();
        user.setId(2L);
        user.setUsername("bob");
        user.setEmail(null);

        UserBanWriteEvent event = mock(UserBanWriteEvent.class);
        when(event.getUser()).thenReturn(user);

        // Act
        listener.onUserBan(event);

        // Assert
        verifyNoInteractions(emailService);
    }

    @Test
    void shouldNotSendEmailIfEmailIsEmpty() {
        // Arrange
        User user = new User();
        user.setId(3L);
        user.setUsername("charlie");
        user.setEmail("");

        UserBanWriteEvent event = mock(UserBanWriteEvent.class);
        when(event.getUser()).thenReturn(user);

        // Act
        listener.onUserBan(event);

        // Assert
        verifyNoInteractions(emailService);
    }

    @Test
    void shouldHandleEmailServiceExceptionGracefully() {
        // Arrange
        User user = new User();
        user.setId(4L);
        user.setUsername("david");
        user.setEmail("david@example.com");

        UserBanWriteEvent event = mock(UserBanWriteEvent.class);
        when(event.getUser()).thenReturn(user);
        when(event.getReason()).thenReturn("Multiple policy violations");

        doThrow(new RuntimeException("Email service failure"))
                .when(emailService)
                .sendBanNotification(anyString(), anyString(), anyString());

        // Act
        listener.onUserBan(event);

        // Assert
        verify(emailService).sendBanNotification(
                eq("david@example.com"),
                eq("david"),
                eq("Multiple policy violations")
        );
    }
}
