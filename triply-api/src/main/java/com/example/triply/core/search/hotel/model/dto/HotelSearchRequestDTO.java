package com.example.triply.core.search.hotel.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class HotelSearchRequestDTO {
    BigDecimal maxPrice;
    LocalDate checkInDate;
    LocalDate checkOutDate;
    String location;
    Integer guests;
}
