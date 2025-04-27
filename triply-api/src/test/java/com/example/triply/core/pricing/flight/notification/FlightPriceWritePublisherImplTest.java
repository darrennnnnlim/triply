package com.example.triply.core.pricing.flight.notification;

import com.example.triply.core.flight.model.dto.FlightPriceDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class FlightPriceWritePublisherImplTest {

    @Mock
    private NotificationTest listener1;

    @Mock
    private NotificationTest listener2;

    @InjectMocks
    private FlightPriceWritePublisherImpl publisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Mockito.reset(listener1, listener2);
        publisher.clearListeners();
    }

    @Test
    void testAddListener() {
        // Given
        publisher.addListener(listener1);

        TransactionSynchronizationManager.initSynchronization();
        // When
        publisher.publish(Collections.emptyList(), Collections.emptyList());

        for (TransactionSynchronization sync : TransactionSynchronizationManager.getSynchronizations()) {
            sync.afterCommit();
        }
        // Then
        verify(listener1, times(1)).onPriceUpdate(any(FlightPriceWriteEvent.class));

        TransactionSynchronizationManager.clearSynchronization();
        publisher.removeListener(listener1);
    }

    @Test
    void testRemoveListener() {
        // Given
        publisher.addListener(listener1);
        publisher.addListener(listener2);
        publisher.removeListener(listener1);

        TransactionSynchronizationManager.initSynchronization();
        // When
        publisher.publish(Collections.emptyList(), Collections.emptyList());

        for (TransactionSynchronization sync : TransactionSynchronizationManager.getSynchronizations()) {
            sync.afterCommit();
        }

        // Then
        verify(listener1, never()).onPriceUpdate(any(FlightPriceWriteEvent.class));
        verify(listener2, times(1)).onPriceUpdate(any(FlightPriceWriteEvent.class));

        TransactionSynchronizationManager.clearSynchronization();
        publisher.removeListener(listener2);
    }

    @Test
    void testPublish_withoutTransaction() {
        // Given
        publisher.addListener(listener1);
        publisher.addListener(listener2);
        List<FlightPriceDTO> oldPrices = Arrays.asList(new FlightPriceDTO(), new FlightPriceDTO());
        List<FlightPriceDTO> newPrices = Collections.singletonList(new FlightPriceDTO());
        FlightPriceWriteEvent event = new FlightPriceWriteEvent(oldPrices, newPrices);

        TransactionSynchronizationManager.initSynchronization();
        // When
        publisher.publish(oldPrices, newPrices);

        for (TransactionSynchronization sync : TransactionSynchronizationManager.getSynchronizations()) {
            sync.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK);
        }

        // Then
        verify(listener1, never()).onPriceUpdate(event);
        verify(listener2, never()).onPriceUpdate(event);

        TransactionSynchronizationManager.clearSynchronization();
        publisher.removeListener(listener1);
        publisher.removeListener(listener2);
    }

    @Test
    void testPublish_withTransaction() {
        // Given
        publisher.addListener(listener1);
        publisher.addListener(listener2);
        List<FlightPriceDTO> oldPrices = Arrays.asList(new FlightPriceDTO(), new FlightPriceDTO());
        List<FlightPriceDTO> newPrices = Collections.singletonList(new FlightPriceDTO());

        // Simulate active transaction
        TransactionSynchronizationManager.initSynchronization();

        // When
        publisher.publish(oldPrices, newPrices);

        // Then before commit
        verify(listener1, never()).onPriceUpdate(any(FlightPriceWriteEvent.class));
        verify(listener2, never()).onPriceUpdate(any(FlightPriceWriteEvent.class));

        for (TransactionSynchronization sync : TransactionSynchronizationManager.getSynchronizations()) {
            sync.afterCommit();
        }

        // Then after commit
        verify(listener1, times(1)).onPriceUpdate(any(FlightPriceWriteEvent.class));
        verify(listener2, times(1)).onPriceUpdate(any(FlightPriceWriteEvent.class));

        // Clean up
        TransactionSynchronizationManager.clearSynchronization();
        publisher.removeListener(listener1);
        publisher.removeListener(listener2);
    }

    @Test
    void testPublish_noListeners() {
        // Given
        publisher.addListener(listener1);
        publisher.addListener(listener2);
        publisher.removeListener(listener1);
        publisher.removeListener(listener2);
        List<FlightPriceDTO> oldPrices = Arrays.asList(new FlightPriceDTO(), new FlightPriceDTO());
        List<FlightPriceDTO> newPrices = Collections.singletonList(new FlightPriceDTO());

        TransactionSynchronizationManager.initSynchronization();
        // When
        publisher.publish(oldPrices, newPrices);

        for (TransactionSynchronization sync : TransactionSynchronizationManager.getSynchronizations()) {
            sync.afterCommit();
        }
        // Then
        TransactionSynchronizationManager.clearSynchronization();
        verify(listener1, never()).onPriceUpdate(any(FlightPriceWriteEvent.class));
        verify(listener2, never()).onPriceUpdate(any(FlightPriceWriteEvent.class));
    }
}