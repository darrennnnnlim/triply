package com.example.triply.core.booking.dto.hotel;

import com.example.triply.common.dto.MutableDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class HotelRoomTypeDTO extends MutableDTO {
    private Long id;
    private Long hotelId;
    private String name;
    private BigDecimal basePrice;
    private int capacity;
}
