package com.example.triply.core.pricing.hotel.notification;

public interface HotelRoomTypeListener {
    void onPriceUpdate(HotelRoomTypeWriteEvent event);
}