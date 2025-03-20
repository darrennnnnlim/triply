package com.example.triply.core.booking.dto.flight;

import com.example.triply.common.dto.MutableDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FlightBookingAddonDTO extends MutableDTO {

    private Long id;
    private Long flightBookingId;
    private Long flightAddonId;
    private BigDecimal price;
    private int quantity;

    private FlightAddonDTO flightAddon;
}
