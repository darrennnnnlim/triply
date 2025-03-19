package com.example.triply.core.booking.dto.hotel;

import com.example.triply.common.dto.MutableDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelDTO extends MutableDTO {
    private Long id;
    private String name;
    private String location;
    private String description;
}
