package com.example.triply.core.flight.publisher;

import com.example.triply.core.flight.model.dto.FlightPriceDTO;

import java.util.List;

public interface FlightPriceWritePublisher {
    /**
     * Publishes an event indicating that flight prices have been updated.
     *
     * @param oldPrices The list of flight price DTOs representing the state *before* the update.
     * @param newPrices The list of flight price DTOs representing the state *after* the update.
     */
    void publish(List<FlightPriceDTO> oldPrices, List<FlightPriceDTO> newPrices);
}