package com.example.triply.core.hotel.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class HotelAddonResponse {

    HotelAddonDTO hotelAddonDTO;
    BigDecimal price;

}
