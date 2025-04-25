package com.example.triply.core.booking.event;

import com.example.triply.core.booking.entity.Booking;

public class BookingConfirmedEvent {
    private final Booking booking;

    public BookingConfirmedEvent(Booking booking) {
        this.booking = booking;
    }

    public Booking getBooking() {
        return booking;
    }
}