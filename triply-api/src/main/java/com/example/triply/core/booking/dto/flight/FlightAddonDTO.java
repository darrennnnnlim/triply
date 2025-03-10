package com.example.triply.core.booking.dto.flight;

import com.example.triply.common.dto.MutableDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightAddonDTO extends MutableDTO {
    private Long id;
    private String name;
}
