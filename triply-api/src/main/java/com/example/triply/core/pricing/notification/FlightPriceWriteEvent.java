package com.example.triply.core.pricing.notification;

import com.example.triply.core.flight.model.dto.FlightPriceDTO;
import lombok.Getter;

import java.util.List;

@Getter
public class FlightPriceWriteEvent {
    private final List<FlightPriceDTO> oldFlightPrices;
    private final List<FlightPriceDTO> newFlightPrices;

    public FlightPriceWriteEvent(List<FlightPriceDTO> oldFlightPrices, List<FlightPriceDTO> newFlightPrices) {
        this.oldFlightPrices = oldFlightPrices;
        this.newFlightPrices = newFlightPrices;
    }
}
