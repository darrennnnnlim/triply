package com.example.triply.core.hotel.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelPriceDTO {
    private Long hotelId;
    private String hotelName; // Added for better notification content
    private BigDecimal price;
    private String currency;
    // Add other relevant fields like check-in/check-out dates if needed
}