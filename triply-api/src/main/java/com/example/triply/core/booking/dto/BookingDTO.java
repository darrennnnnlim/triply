package com.example.triply.core.booking.dto;

import com.example.triply.common.dto.MutableDTO;
import com.example.triply.core.booking.dto.flight.FlightBookingAddonDTO;
import com.example.triply.core.booking.dto.flight.FlightBookingDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class BookingDTO extends MutableDTO {

    private Long id;
    private Long userId;
    private BigDecimal finalPrice;
    private String status;
    private LocalDateTime bookingTime;

    private FlightBookingDTO flightBooking;
    private HotelBookingDTO hotelBooking;

    private List<FlightBookingAddonDTO> flightBookingAddon;
    private List<HotelBookingAddonDTO> hotelBookingAddon;
}
