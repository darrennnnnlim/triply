package com.example.triply.core.flight.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FlightAddonResponse {

    FlightAddonDTO flightAddonDTO;
    BigDecimal price;

}
