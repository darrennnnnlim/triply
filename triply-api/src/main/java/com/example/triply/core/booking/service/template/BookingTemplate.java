package com.example.triply.core.booking.service.template;

import com.example.triply.core.booking.dto.BookingDTO;
import com.example.triply.core.booking.entity.Booking;

import java.math.BigDecimal;

public abstract class BookingTemplate{

    public final Booking processBooking(BookingDTO request) {
        validateBooking(request);
        BigDecimal addOnPrice = calculateAddonPrice(request);
        calculateTotalPrice(request, addOnPrice);
        Booking booking = createBooking(request);
        confirmBooking(booking);
        return booking;
    }

    protected abstract void validateBooking(BookingDTO request);
    protected abstract BigDecimal calculateAddonPrice(BookingDTO request);
    protected abstract void calculateTotalPrice(BookingDTO request, BigDecimal addOnPrice);
    protected abstract Booking createBooking(BookingDTO request);

    protected abstract void confirmBooking(Booking booking);
}
