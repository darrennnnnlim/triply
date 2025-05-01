package com.example.triply.core.pricing.hotel.notification;

import com.example.triply.core.hotel.model.dto.HotelRoomTypeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;

@Service
public class HotelRoomTypeWritePublisherImpl {
    private final List<HotelRoomTypeListener> listeners;
    private HotelRoomTypeNotificationTest hotelRoomTypeNotificationTest;

    @Autowired
    public HotelRoomTypeWritePublisherImpl(HotelRoomTypeNotificationTest hotelRoomTypeNotificationTest) {
        this.listeners = new ArrayList<>();

        this.hotelRoomTypeNotificationTest = hotelRoomTypeNotificationTest;

        // Register listeners here
        this.addListener(this.hotelRoomTypeNotificationTest);
    }

    public void addListener(HotelRoomTypeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(HotelRoomTypeListener listener) {
        listeners.remove(listener);
    }

    public void clearListeners() { this.listeners.clear(); }

    public void publish(List<HotelRoomTypeDTO> oldHotelRoomTypes, List<HotelRoomTypeDTO> newHotelRoomTypes) {
        HotelRoomTypeWriteEvent event = new HotelRoomTypeWriteEvent(oldHotelRoomTypes, newHotelRoomTypes);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    notifyListeners(event);
                }
            });
        }
    }

    private void notifyListeners(HotelRoomTypeWriteEvent event) {
        for (HotelRoomTypeListener listener : listeners) {
            listener.onPriceUpdate(event);
        }
    }
}
