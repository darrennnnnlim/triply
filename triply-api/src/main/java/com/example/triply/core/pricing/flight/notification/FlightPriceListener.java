package com.example.triply.core.pricing.flight.notification;

public interface FlightPriceListener {
    void onPriceUpdate(FlightPriceWriteEvent event);
}