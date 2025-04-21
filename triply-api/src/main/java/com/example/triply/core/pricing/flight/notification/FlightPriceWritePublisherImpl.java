package com.example.triply.core.pricing.flight.notification;

import com.example.triply.core.flight.model.dto.FlightPriceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
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
        this.addListener(notificationTest);
    }

    public void addListener(FlightPriceListener listener) {
        listeners.add(listener);
    }

    public void removeListener(FlightPriceListener listener) {
        listeners.remove(listener);
    }

    public void publish(List<FlightPriceDTO> oldFlightPrices, List<FlightPriceDTO> newFlightPrices) {
        FlightPriceWriteEvent event = new FlightPriceWriteEvent(oldFlightPrices, newFlightPrices);
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

    private void notifyListeners(FlightPriceWriteEvent event) {
        for (FlightPriceListener listener : listeners) {
            listener.onPriceUpdate(event);
        }
    }
}
