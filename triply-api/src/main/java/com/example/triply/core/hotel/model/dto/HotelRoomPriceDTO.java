package com.example.triply.core.hotel.model.dto;

import com.example.triply.common.dto.MutableDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
public class HotelRoomPriceDTO extends MutableDTO {
    private Long id;
    private Long hotelRoomTypeId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal price;
}
