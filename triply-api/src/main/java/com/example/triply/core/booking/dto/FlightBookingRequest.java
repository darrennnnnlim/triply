package com.example.triply.core.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FlightBookingRequest {
    @NotNull
    private Long userId;

    @NotNull
    private Long flightId;

    @NotBlank
    private String seatClass; // e.g., ECONOMY, BUSINESS

    @NotBlank
    private String seatNumber;
}
