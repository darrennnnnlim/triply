package com.example.triply.core.booking.dto.flight;

import com.example.triply.common.dto.MutableDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FlightDTO extends MutableDTO {
    private Long id;
    private Long airlineId;
    private String flightNumber;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    private AirlineDTO airline;
}
