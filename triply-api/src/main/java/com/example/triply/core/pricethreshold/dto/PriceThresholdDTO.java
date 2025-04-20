package com.example.triply.core.pricethreshold.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceThresholdDTO {

    private Long id;
    private Long userId; // Include user ID for reference
    private Long flightId; // Nullable
    private Long hotelId;  // Nullable
    private BigDecimal thresholdPrice;

    // Optional: Include flight/hotel names or other relevant details if needed
    // private String flightName;
    // private String hotelName;
}