package com.example.triply.core.auth.notification;


import com.example.triply.core.auth.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class UserRegistrationWritePublisherImplTest {

    private UserRegistrationListener listener1;
    private UserRegistrationListener listener2;
    private UserRegistrationWriteEvent event;
    private UserRegistrationWritePublisherImpl publisher;

    @BeforeEach
    void setUp() {
        //Disable logger for unit tests
        org.slf4j.Logger root = org.slf4j.LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        if (root instanceof ch.qos.logback.classic.Logger logger) {
            logger.setLevel(ch.qos.logback.classic.Level.OFF);
        }

        listener1 = mock(UserRegistrationEmailNotificationListener.class);
        listener2 = mock(UserRegistrationEmailNotificationListener.class);
        event = mock(UserRegistrationWriteEvent.class);
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("testuser");
        when(event.getUser()).thenReturn(user);
        doNothing().when(listener1).onUserRegistration(any(UserRegistrationWriteEvent.class));
        doNothing().when(listener2).onUserRegistration(any(UserRegistrationWriteEvent.class));
    }

    @Test
    void shouldPublishEventToAllListeners() {
        publisher = new UserRegistrationWritePublisherImpl(List.of(listener1, listener2));

        publisher.publish(event);

        verify(listener1, times(1)).onUserRegistration(event);
        verify(listener2, times(1)).onUserRegistration(event);
    }

    @Test
    void shouldNotFailIfListenerThrowsException() {
        doThrow(new RuntimeException("error")).when(listener1).onUserRegistration(event);
        publisher = new UserRegistrationWritePublisherImpl(List.of(listener1, listener2));

        publisher.publish(event);

        verify(listener1).onUserRegistration(event);
        verify(listener2).onUserRegistration(event); // still called
    }

    @Test
    void shouldAddAndRemoveListenersDynamically() {
        publisher = new UserRegistrationWritePublisherImpl(Collections.emptyList());

        publisher.addListener(listener1);
        publisher.publish(event);
        verify(listener1, times(1)).onUserRegistration(event);

        publisher.removeListener(listener1);
        publisher.publish(event);
        verifyNoMoreInteractions(listener1);
    }

    @Test
    void constructorShouldHandleNullListeners() {
        publisher = new UserRegistrationWritePublisherImpl(null);
        publisher.publish(event); // should not throw
    }
}
