package com.example.triply.core.pricing.hotel.notification;

import com.example.triply.core.hotel.model.dto.HotelRoomTypeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

class HotelRoomTypeWritePublisherImplTest {

    @Mock
    private HotelRoomTypeListener listener1;

    @Mock
    private HotelRoomTypeListener listener2;

    @InjectMocks
    private HotelRoomTypeWritePublisherImpl publisher;

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
        verify(listener1, times(1)).onPriceUpdate(any(HotelRoomTypeWriteEvent.class));

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
        verify(listener1, never()).onPriceUpdate(any(HotelRoomTypeWriteEvent.class));
        verify(listener2, times(1)).onPriceUpdate(any(HotelRoomTypeWriteEvent.class));

        TransactionSynchronizationManager.clearSynchronization();
        publisher.removeListener(listener2);
    }

    @Test
    void testPublish_withoutTransaction() {
        // Given
        publisher.addListener(listener1);
        publisher.addListener(listener2);
        List<HotelRoomTypeDTO> oldHotelRoomTypes = Arrays.asList(new HotelRoomTypeDTO(), new HotelRoomTypeDTO());
        List<HotelRoomTypeDTO> newHotelRoomTypes = Collections.singletonList(new HotelRoomTypeDTO());
        HotelRoomTypeWriteEvent event = new HotelRoomTypeWriteEvent(oldHotelRoomTypes, newHotelRoomTypes);

        TransactionSynchronizationManager.initSynchronization();
        // When
        publisher.publish(oldHotelRoomTypes, newHotelRoomTypes);

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
        List<HotelRoomTypeDTO> oldHotelRoomTypes = Arrays.asList(new HotelRoomTypeDTO(), new HotelRoomTypeDTO());
        List<HotelRoomTypeDTO> newHotelRoomTypes = Collections.singletonList(new HotelRoomTypeDTO());

        // Simulate active transaction
        TransactionSynchronizationManager.initSynchronization();

        // When
        publisher.publish(oldHotelRoomTypes, newHotelRoomTypes);

        // Then before commit
        verify(listener1, never()).onPriceUpdate(any(HotelRoomTypeWriteEvent.class));
        verify(listener2, never()).onPriceUpdate(any(HotelRoomTypeWriteEvent.class));

        for (TransactionSynchronization sync : TransactionSynchronizationManager.getSynchronizations()) {
            sync.afterCommit();
        }

        // Then after commit
        verify(listener1, times(1)).onPriceUpdate(any(HotelRoomTypeWriteEvent.class));
        verify(listener2, times(1)).onPriceUpdate(any(HotelRoomTypeWriteEvent.class));

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
        List<HotelRoomTypeDTO> oldHotelRoomTypes = Arrays.asList(new HotelRoomTypeDTO(), new HotelRoomTypeDTO());
        List<HotelRoomTypeDTO> newHotelRoomTypes = Collections.singletonList(new HotelRoomTypeDTO());

        TransactionSynchronizationManager.initSynchronization();
        // When
        publisher.publish(oldHotelRoomTypes, newHotelRoomTypes);

        for (TransactionSynchronization sync : TransactionSynchronizationManager.getSynchronizations()) {
            sync.afterCommit();
        }
        // Then
        TransactionSynchronizationManager.clearSynchronization();
        verify(listener1, never()).onPriceUpdate(any(HotelRoomTypeWriteEvent.class));
        verify(listener2, never()).onPriceUpdate(any(HotelRoomTypeWriteEvent.class));
    }
}