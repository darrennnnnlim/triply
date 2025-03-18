package com.example.triply.core.booking.service;

import com.example.triply.core.booking.entity.Booking;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public abstract class AbstractBookingService<T> {

    public final Booking processBooking(T request) {
        validateRequest(request);
        checkAvailability(request);
        BigDecimal price = calculateTotalPrice(request);
        Booking booking = createBooking(request, price);
        confirmBooking(booking);
        return booking;
    }

    protected abstract void validateRequest(T request);
    protected abstract void checkAvailability(T request);
    protected abstract BigDecimal calculateTotalPrice(T request);
    protected abstract Booking createBooking(T request, BigDecimal price);
    protected abstract void confirmBooking(Booking booking);
}
