package com.example.triply.core.hotel.model.dto;

import com.example.triply.common.dto.MutableDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelAddonBasicDTO extends MutableDTO {
    private Long id;
    private String name;
}
