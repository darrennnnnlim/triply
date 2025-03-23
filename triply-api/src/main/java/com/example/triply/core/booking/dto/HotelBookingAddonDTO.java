package com.example.triply.core.booking.dto;

import com.example.triply.common.dto.MutableDTO;
import com.example.triply.core.booking.dto.hotel.HotelAddonDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class HotelBookingAddonDTO extends MutableDTO {

    private Long id;
    private Long hotelBookingId;
    private Long hotelAddonId;
    private int quantity;
    private BigDecimal totalPrice;

    private HotelAddonDTO hotelAddon;
}
