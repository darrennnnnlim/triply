package com.example.triply.core.flight.model.dto;

import com.example.triply.common.dto.MutableDTO;
import com.example.triply.core.flight.model.entity.Flight;
import com.example.triply.core.flight.model.entity.FlightClass;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class FlightPriceDTO extends MutableDTO {
    private Long id;
    private FlightDTO flightDTO;
    private FlightClassDTO flightClassDTO;

    // Add primitive fields needed by the listener
    private String flightNumber;
    private String origin;
    private String destination;
    private String flightClassName;

    private LocalDateTime departureDate;
    private BigDecimal basePrice;
    private BigDecimal discount = BigDecimal.valueOf(1.00);
    private BigDecimal surgeMultiplier = BigDecimal.valueOf(1.0);
}
