package com.example.triply.core.pricing.flight.notification;

import com.example.triply.core.flight.model.dto.FlightPriceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;

@Service
public class FlightPriceWritePublisherImpl {
    private final List<FlightPriceListener> listeners;
    private final NotificationTest notificationTest;

    @Autowired
    public FlightPriceWritePublisherImpl(NotificationTest notificationTest) {
        this.listeners = new ArrayList<>();

        this.notificationTest = notificationTest;

        // Register listeners here
        this.addListener(this.notificationTest);
    }

    public void addListener(FlightPriceListener listener) {
        listeners.add(listener);
    }

    public void removeListener(FlightPriceListener listener) {
        listeners.remove(listener);
    }

    public void clearListeners() {
        listeners.clear();
    }

    public void publish(List<FlightPriceDTO> oldFlightPrices, List<FlightPriceDTO> newFlightPrices) {
        FlightPriceWriteEvent event = new FlightPriceWriteEvent(oldFlightPrices, newFlightPrices);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    notifyListeners(event);
                }
            });
        }
    }

    private void notifyListeners(FlightPriceWriteEvent event) {
        for (FlightPriceListener listener : listeners) {
            listener.onPriceUpdate(event);
        }
    }
}
