package com.example.triply.core.auth.notification;

import com.example.triply.core.auth.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class UserUnbanWritePublisherImplTest {

    private UserUnbanListener listener1;
    private UserUnbanListener listener2;
    private UserUnbanWriteEvent event;
    private UserUnbanWritePublisherImpl publisher;

    @BeforeEach
    void setUp() {
        //Disable logger for unit tests
        org.slf4j.Logger root = org.slf4j.LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        if (root instanceof ch.qos.logback.classic.Logger logger) {
            logger.setLevel(ch.qos.logback.classic.Level.OFF);
        }

        listener1 = mock(UserUnbanEmailNotificationListener.class);
        listener2 = mock(UserUnbanEmailNotificationListener.class);
        event = mock(UserUnbanWriteEvent.class);

        User user = mock(User.class);
        when(user.getUsername()).thenReturn("testuser");
        when(event.getUser()).thenReturn(user);

        doNothing().when(listener1).onUserUnban(any(UserUnbanWriteEvent.class));
        doNothing().when(listener2).onUserUnban(any(UserUnbanWriteEvent.class));
    }

    @Test
    void shouldNotifyAllListeners() {
        publisher = new UserUnbanWritePublisherImpl(List.of(listener1, listener2));

        publisher.publish(event);

        verify(listener1).onUserUnban(event);
        verify(listener2).onUserUnban(event);
    }

    @Test
    void shouldCatchExceptionsFromListener() {
        doThrow(new RuntimeException("fail")).when(listener1).onUserUnban(event);
        publisher = new UserUnbanWritePublisherImpl(List.of(listener1, listener2));

        publisher.publish(event);

        verify(listener1).onUserUnban(event);
        verify(listener2).onUserUnban(event); // should still be called
    }

    @Test
    void shouldAddAndRemoveListeners() {
        publisher = new UserUnbanWritePublisherImpl(Collections.emptyList());

        publisher.addListener(listener1);
        publisher.publish(event);
        verify(listener1).onUserUnban(event);

        publisher.removeListener(listener1);
        publisher.publish(event);
        verifyNoMoreInteractions(listener1);
    }

    @Test
    void shouldHandleNullListenersList() {
        publisher = new UserUnbanWritePublisherImpl(null);
        publisher.publish(event); // should not throw
    }

    @Test
    void shouldNotAddDuplicateListener() {
        publisher = new UserUnbanWritePublisherImpl(Collections.emptyList());

        publisher.addListener(listener1);
        publisher.addListener(listener1); // add again

        publisher.publish(event);

        verify(listener1, times(1)).onUserUnban(event); // only called once
    }
}
