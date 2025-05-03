package com.example.triply.core.auth.notification;

import com.example.triply.core.auth.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class UserBanWritePublisherImplTest {

    private UserBanListener listener1;
    private UserBanListener listener2;
    private UserBanWriteEvent event;
    private UserBanWritePublisherImpl publisher;

    @BeforeEach
    void setUp() {
        //Disable logger for unit tests
        org.slf4j.Logger root = org.slf4j.LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        if (root instanceof ch.qos.logback.classic.Logger logger) {
            logger.setLevel(ch.qos.logback.classic.Level.OFF);
        }

        listener1 = mock(UserBanEmailNotificationListener.class);
        listener2 = mock(UserBanEmailNotificationListener.class);
        event = mock(UserBanWriteEvent.class);

        // Mocking nested user.getUsername()
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("testuser");
        when(event.getUser()).thenReturn(user);
        doNothing().when(listener1).onUserBan(any(UserBanWriteEvent.class));
        doNothing().when(listener2).onUserBan(any(UserBanWriteEvent.class));
    }

    @Test
    void shouldNotifyAllListeners() {
        publisher = new UserBanWritePublisherImpl(List.of(listener1, listener2));

        publisher.publish(event);

        verify(listener1).onUserBan(event);
        verify(listener2).onUserBan(event);
    }

    @Test
    void shouldCatchExceptionsFromListeners() {
        doThrow(new RuntimeException("Listener failed")).when(listener1).onUserBan(event);
        publisher = new UserBanWritePublisherImpl(List.of(listener1, listener2));

        publisher.publish(event);

        verify(listener1).onUserBan(event);
        verify(listener2).onUserBan(event); // still gets called despite listener1 failing
    }

    @Test
    void shouldAllowDynamicAddAndRemove() {
        publisher = new UserBanWritePublisherImpl(Collections.emptyList());

        publisher.addListener(listener1);
        publisher.publish(event);
        verify(listener1).onUserBan(event);

        publisher.removeListener(listener1);
        publisher.publish(event);
        verifyNoMoreInteractions(listener1);
    }

    @Test
    void constructorHandlesNullListeners() {
        publisher = new UserBanWritePublisherImpl(null);
        publisher.publish(event); // should not throw
    }
}
