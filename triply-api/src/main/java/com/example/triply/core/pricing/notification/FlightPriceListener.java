package com.example.triply.core.pricing.notification;

public interface FlightPriceListener {
    void onPriceUpdate(FlightPriceWriteEvent event);
}