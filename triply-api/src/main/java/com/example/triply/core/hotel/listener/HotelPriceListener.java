package com.example.triply.core.hotel.listener;

import com.example.triply.core.hotel.event.HotelPriceWriteEvent;

public interface HotelPriceListener {
    void onPriceUpdate(HotelPriceWriteEvent event);
}