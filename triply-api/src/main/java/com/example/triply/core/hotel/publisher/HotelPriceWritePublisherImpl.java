package com.example.triply.core.hotel.publisher;

import com.example.triply.core.hotel.event.HotelPriceWriteEvent;
import com.example.triply.core.hotel.listener.HotelPriceEmailNotificationListener; // To be created
import com.example.triply.core.hotel.listener.HotelPriceListener;
import com.example.triply.core.hotel.model.dto.HotelPriceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;

@Service
public class HotelPriceWritePublisherImpl {

    private final List<HotelPriceListener> listeners;
    private final HotelPriceListener emailNotificationListener; // Inject listener via interface

    @Autowired
    public HotelPriceWritePublisherImpl(
            HotelPriceListener emailNotificationListener // Inject listener via interface
    ) {
        this.listeners = new ArrayList<>();
        this.emailNotificationListener = emailNotificationListener;

        // Register listeners here
        this.addListener(this.emailNotificationListener);
    }

    public void addListener(HotelPriceListener listener) {
        listeners.add(listener);
    }

    public void removeListener(HotelPriceListener listener) {
        listeners.remove(listener);
    }

    public void publish(List<HotelPriceDTO> oldHotelPrices, List<HotelPriceDTO> newHotelPrices) {
        HotelPriceWriteEvent event = new HotelPriceWriteEvent(oldHotelPrices, newHotelPrices);
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

    private void notifyListeners(HotelPriceWriteEvent event) {
        for (HotelPriceListener listener : listeners) {
            listener.onPriceUpdate(event);
        }
    }
}