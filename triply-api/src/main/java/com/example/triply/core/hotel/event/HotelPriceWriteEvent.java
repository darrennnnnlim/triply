package com.example.triply.core.hotel.event;

import com.example.triply.core.hotel.model.dto.HotelPriceDTO;

import java.util.List;

public class HotelPriceWriteEvent {

    private final List<HotelPriceDTO> oldHotelPrices;
    private final List<HotelPriceDTO> newHotelPrices;

    public HotelPriceWriteEvent(List<HotelPriceDTO> oldHotelPrices, List<HotelPriceDTO> newHotelPrices) {
        this.oldHotelPrices = oldHotelPrices;
        this.newHotelPrices = newHotelPrices;
    }

    public List<HotelPriceDTO> getOldHotelPrices() {
        return oldHotelPrices;
    }

    public List<HotelPriceDTO> getNewHotelPrices() {
        return newHotelPrices;
    }
}