package com.example.triply.core.search.flight.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class FlightSearchRequestDTO {
    private String origin;
    private String destination;
    private LocalDate departureDate;
    private LocalDate arrivalDate;
    private BigDecimal maxPrice;
}