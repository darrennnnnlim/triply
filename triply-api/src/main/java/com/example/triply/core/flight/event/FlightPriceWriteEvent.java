package com.example.triply.core.flight.event;

import com.example.triply.core.flight.model.dto.FlightPriceDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class FlightPriceWriteEvent extends ApplicationEvent {

    private final List<FlightPriceDTO> oldPrices;
    private final List<FlightPriceDTO> newPrices;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     * @param oldPrices the list of flight prices before the update
     * @param newPrices the list of flight prices after the update
     */
    public FlightPriceWriteEvent(Object source, List<FlightPriceDTO> oldPrices, List<FlightPriceDTO> newPrices) {
        super(source);
        this.oldPrices = oldPrices;
        this.newPrices = newPrices;
    }

    // Explicitly adding getters as Lombok might not be processed correctly
    public List<FlightPriceDTO> getOldPrices() {
        return oldPrices;
    }

    public List<FlightPriceDTO> getNewPrices() {
        return newPrices;
    }
}