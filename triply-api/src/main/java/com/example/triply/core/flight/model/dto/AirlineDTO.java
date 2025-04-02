package com.example.triply.core.flight.model.dto;

import com.example.triply.common.dto.MutableDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AirlineDTO extends MutableDTO {
    private Long id;
    private String name;
    private String code;
}
