package com.example.triply.core.flight.publisher.impl;

import com.example.triply.core.flight.event.FlightPriceWriteEvent;
import com.example.triply.core.flight.model.dto.FlightPriceDTO;
import com.example.triply.core.flight.publisher.FlightPriceWritePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

// @Component
@RequiredArgsConstructor
public class FlightPriceWritePublisherImpl implements FlightPriceWritePublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(List<FlightPriceDTO> oldPrices, List<FlightPriceDTO> newPrices) {
        FlightPriceWriteEvent event = new FlightPriceWriteEvent(this, oldPrices, newPrices);
        eventPublisher.publishEvent(event);
    }
}