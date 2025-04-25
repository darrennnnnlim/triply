package com.example.triply.core.pricing.hotel.notification;

import com.example.triply.core.hotel.model.dto.HotelRoomTypeDTO;
import lombok.Getter;

import java.util.List;

@Getter
public class HotelRoomTypeWriteEvent {
    private final List<HotelRoomTypeDTO> oldHotelRoomTypes;
    private final List<HotelRoomTypeDTO> newHotelRoomTypes;

    public HotelRoomTypeWriteEvent(List<HotelRoomTypeDTO> oldHotelRoomTypes, List<HotelRoomTypeDTO> newHotelRoomTypes) {
        this.oldHotelRoomTypes = oldHotelRoomTypes;
        this.newHotelRoomTypes = newHotelRoomTypes;
    }
}
