package com.example.triply.core.pricing.flight.notification;

import com.example.triply.core.flight.model.dto.FlightPriceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier; // Import Qualifier
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;

@Service
public class FlightPriceWritePublisherImpl {
    private final List<FlightPriceListener> listeners;
    private final NotificationTest notificationTest;
    private final FlightPriceListener emailNotificationListener; // Add email listener field

    @Autowired
    public FlightPriceWritePublisherImpl(NotificationTest notificationTest,
                                         @Qualifier("flightPriceEmailNotificationListener") FlightPriceListener emailNotificationListener // Qualify the listener bean
                                        ) {
        this.listeners = new ArrayList<>();

        this.notificationTest = notificationTest;
        this.emailNotificationListener = emailNotificationListener; // Assign injected listener

        // Register listeners here
        this.addListener(this.notificationTest);
        this.addListener(notificationTest);
        this.addListener(this.emailNotificationListener); // Register email listener
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
