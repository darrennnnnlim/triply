package com.example.triply.core.booking.service;

import com.example.triply.core.booking.dto.FlightBookingRequest;
import com.example.triply.core.booking.entity.Booking;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Transactional
public class FlightBookingService extends AbstractBookingService<FlightBookingRequest> {
    @Override
    protected void validateRequest(FlightBookingRequest request) {

    }

    @Override
    protected void checkAvailability(FlightBookingRequest request) {

    }

    @Override
    protected BigDecimal calculateTotalPrice(FlightBookingRequest request) {
        return null;
    }

    @Override
    protected Booking createBooking(FlightBookingRequest request, BigDecimal price) {
        return null;
    }

    @Override
    protected void confirmBooking(Booking booking) {

    }
}
