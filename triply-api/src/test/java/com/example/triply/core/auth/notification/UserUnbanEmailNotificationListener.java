package com.example.triply.core.auth.notification;

import com.example.triply.common.service.EmailService;
import com.example.triply.core.auth.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class UserUnbanEmailNotificationListenerTest {

    private EmailService emailService;
    private UserUnbanEmailNotificationListener listener;

    @BeforeEach
    void setUp() {//Disable logger for unit tests
        org.slf4j.Logger root = org.slf4j.LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        if (root instanceof ch.qos.logback.classic.Logger logger) {
            logger.setLevel(ch.qos.logback.classic.Level.OFF);
        }

        emailService = mock(EmailService.class);
        listener = new UserUnbanEmailNotificationListener(emailService);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void shouldSendUnbanEmailForValidUser() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("johndoe");
        user.setEmail("john@example.com");

        UserUnbanWriteEvent event = mock(UserUnbanWriteEvent.class);
        when(event.getUser()).thenReturn(user);

        // Act
        listener.onUserUnban(event);

        // Assert
        verify(emailService).sendEmail(
                eq("john@example.com"),
                eq("Your Account Has Been Reinstated"),
                contains("Dear johndoe")
        );
    }

    @Test
    void shouldNotSendEmailIfUserIsNull() {
        UserUnbanWriteEvent event = mock(UserUnbanWriteEvent.class);
        when(event.getUser()).thenReturn(null);

        listener.onUserUnban(event);

        verifyNoInteractions(emailService);
    }

    @Test
    void shouldNotSendEmailIfEmailIsNull() {
        User user = new User();
        user.setUsername("janedoe");
        user.setEmail(null);

        UserUnbanWriteEvent event = mock(UserUnbanWriteEvent.class);
        when(event.getUser()).thenReturn(user);

        listener.onUserUnban(event);

        verifyNoInteractions(emailService);
    }

    @Test
    void shouldNotSendEmailIfEmailIsEmpty() {
        User user = new User();
        user.setUsername("janedoe");
        user.setEmail("");

        UserUnbanWriteEvent event = mock(UserUnbanWriteEvent.class);
        when(event.getUser()).thenReturn(user);

        listener.onUserUnban(event);

        verifyNoInteractions(emailService);
    }

    @Test
    void shouldHandleEmailServiceExceptionGracefully() {
        User user = new User();
        user.setId(3L);
        user.setUsername("alex");
        user.setEmail("alex@example.com");

        doThrow(new RuntimeException("SMTP failure"))
                .when(emailService)
                .sendEmail(anyString(), anyString(), anyString());

        UserUnbanWriteEvent event = mock(UserUnbanWriteEvent.class);
        when(event.getUser()).thenReturn(user);

        listener.onUserUnban(event);

        verify(emailService).sendEmail(
                eq("alex@example.com"),
                eq("Your Account Has Been Reinstated"),
                contains("Dear alex")
        );
    }
}
