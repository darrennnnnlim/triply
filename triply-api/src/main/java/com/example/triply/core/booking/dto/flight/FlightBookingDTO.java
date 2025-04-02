package com.example.triply.core.booking.dto.flight;

import com.example.triply.common.dto.MutableDTO;
import com.example.triply.core.flight.model.dto.FlightDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FlightBookingDTO extends MutableDTO {
    private Long id;
    private Long flightId;
    private Long flightClassId;
    private Long bookingId;
    private Long userId;
    private LocalDateTime departureDate;

    private FlightDTO flight;
}
