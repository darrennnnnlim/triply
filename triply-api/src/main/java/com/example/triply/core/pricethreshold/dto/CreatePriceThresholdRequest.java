package com.example.triply.core.pricethreshold.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreatePriceThresholdRequest {

    // userId will likely be derived from the authenticated user principal in the service/controller
    // private Long userId;

    private Long flightId; // Either flightId or hotelId must be provided

    private Long hotelId;  // Either flightId or hotelId must be provided

    @NotNull(message = "Threshold price cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Threshold price must be positive")
    private BigDecimal thresholdPrice;

    // Add validation to ensure either flightId or hotelId is present, but not both.
    // This can be done via a custom validator or within the service layer.
}