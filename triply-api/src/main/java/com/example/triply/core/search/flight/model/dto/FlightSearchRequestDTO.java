package com.example.triply.core.search.flight.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class FlightSearchRequestDTO {
    private String origin;
    private String destination;
    private LocalDateTime departureDate;
    private LocalDateTime arrivalDate;
    private BigDecimal maxPrice;

    //Other properties
//    private String flightNumber;
//    private Long flightClassId;
//    private String flightClassName;
//    private Long airlineId;
//    private String airlineName;
}