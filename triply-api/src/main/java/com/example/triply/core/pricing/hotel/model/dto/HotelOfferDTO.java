package com.example.triply.core.pricing.hotel.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class HotelOfferDTO {
    BigDecimal totalPrice;
    LocalDate checkInDate;
    LocalDate checkOutDate;
    Integer capacity;
    String location;

    Long hotelId;
    String hotelName;
    Long hotelRoomTypeId;
    String hotelRoomTypeName;
}
