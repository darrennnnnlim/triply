package com.example.triply.core.hotel.model.dto;

import com.example.triply.common.dto.MutableDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelRoomTypeBasicDTO extends MutableDTO {
    private Long id;
    private Long hotelId;
    private String name;
    private int capacity;
}
