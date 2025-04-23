package com.example.triply.core.pricing.hotel.notification;

import com.example.triply.core.hotel.model.dto.HotelRoomTypeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;

@Service
public class HotelRoomTypeWritePublisherImpl {
    private final List<HotelRoomTypeListener> listeners;
    private final HotelRoomTypeNotificationTest hotelRoomTypeNotificationTest;

    @Autowired
    public HotelRoomTypeWritePublisherImpl(HotelRoomTypeNotificationTest hotelRoomTypeNotificationTest) {
        this.listeners = new ArrayList<>();

        this.hotelRoomTypeNotificationTest = hotelRoomTypeNotificationTest;

        // Register listeners here
        this.addListener(hotelRoomTypeNotificationTest);
    }

    public void addListener(HotelRoomTypeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(HotelRoomTypeListener listener) {
        listeners.remove(listener);
    }

    public void publish(List<HotelRoomTypeDTO> oldHotelRoomTypes, List<HotelRoomTypeDTO> newHotelRoomTypes) {
        HotelRoomTypeWriteEvent event = new HotelRoomTypeWriteEvent(oldHotelRoomTypes, newHotelRoomTypes);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    notifyListeners(event);
                }
            });
        } else {
            notifyListeners(event);
        }
    }

    private void notifyListeners(HotelRoomTypeWriteEvent event) {
        for (HotelRoomTypeListener listener : listeners) {
            listener.onPriceUpdate(event);
        }
    }
}
