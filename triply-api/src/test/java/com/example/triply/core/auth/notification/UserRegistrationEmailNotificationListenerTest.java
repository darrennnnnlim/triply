package com.example.triply.core.auth.notification;

import com.example.triply.common.service.EmailService;
import com.example.triply.core.auth.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class UserRegistrationEmailNotificationListenerTest {

    private EmailService emailService;
    private UserRegistrationEmailNotificationListener listener;

    @BeforeEach
    void setUp() {
        //Disable logger for unit tests
        org.slf4j.Logger root = org.slf4j.LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        if (root instanceof ch.qos.logback.classic.Logger logger) {
            logger.setLevel(ch.qos.logback.classic.Level.OFF);
        }

        emailService = mock(EmailService.class);
        listener = new UserRegistrationEmailNotificationListener(emailService);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void shouldSendEmailForValidUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setEmail("alice@example.com");

        UserRegistrationWriteEvent event = mock(UserRegistrationWriteEvent.class);
        when(event.getUser()).thenReturn(user);

        listener.onUserRegistration(event);

        verify(emailService).sendEmail(
                eq("alice@example.com"),
                eq("Welcome to Triply!"),
                contains("Hi alice") // verify personalized content
        );
    }

    @Test
    void shouldNotSendEmailIfUserIsNull() {
        UserRegistrationWriteEvent event = mock(UserRegistrationWriteEvent.class);
        when(event.getUser()).thenReturn(null);

        listener.onUserRegistration(event);

        verifyNoInteractions(emailService);
    }

    @Test
    void shouldNotSendEmailIfEmailIsNullOrEmpty() {
        User user = new User();
        user.setId(2L);
        user.setUsername("bob");
        user.setEmail(""); // or setEmail(null)

        UserRegistrationWriteEvent event = mock(UserRegistrationWriteEvent.class);
        when(event.getUser()).thenReturn(user);

        listener.onUserRegistration(event);

        verifyNoInteractions(emailService);
    }

    @Test
    void shouldCatchAndLogEmailServiceException() {
        User user = new User();
        user.setId(3L);
        user.setUsername("charlie");
        user.setEmail("charlie@example.com");

        doThrow(new RuntimeException("SMTP failure"))
                .when(emailService)
                .sendEmail(anyString(), anyString(), anyString());

        UserRegistrationWriteEvent event = mock(UserRegistrationWriteEvent.class);
        when(event.getUser()).thenReturn(user);

        listener.onUserRegistration(event);

        verify(emailService).sendEmail(anyString(), anyString(), anyString());
    }
}
