package com.example.triply.core.booking.service;

import com.example.triply.core.booking.dto.FlightBookingRequest;
import com.example.triply.core.booking.dto.FlightBookingResponse;
import com.example.triply.core.booking.entity.Booking;
import com.example.triply.core.booking.entity.flight.FlightBooking;
import com.example.triply.core.booking.repository.FlightBookingRepository;
import com.example.triply.core.ratings.dto.RatingResponse;
import com.example.triply.core.ratings.entity.Ratings;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class FlightBookingService extends AbstractBookingService<FlightBookingRequest> {

    @Autowired
    private FlightBookingRepository flightBookingRepository;

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

    public List<FlightBookingResponse> getBookingByUserId (Long userId){
        List<FlightBooking> flightBooking = flightBookingRepository.findByUserId(userId);
        List<FlightBookingResponse> flightBookingResponse = new ArrayList<>();
        for (FlightBooking booking : flightBooking) {
            FlightBookingResponse resp = new FlightBookingResponse();
            resp.setUserId(userId);
            resp.setBookingId(booking.getBooking().getId());
            resp.setSeatNumber(booking.getSeatNumber());
            resp.setSeatClass(booking.getSeatClass());
            resp.setFlightId(booking.getFlight().getId());
            flightBookingResponse.add(resp);
        }
        return flightBookingResponse;
    }

    public List<FlightBookingResponse> getBookingByBookingId (Long bookingId, Long userId){
        List<FlightBooking> flightBooking = flightBookingRepository.findByBookingId(bookingId);
        List<FlightBookingResponse> flightBookingResponse = new ArrayList<>();
        for (FlightBooking booking : flightBooking) {
            FlightBookingResponse resp = new FlightBookingResponse();
            resp.setUserId(userId);
            resp.setSeatNumber(booking.getSeatNumber());
            resp.setSeatClass(booking.getSeatClass());
            resp.setFlightId(booking.getFlight().getId());
            flightBookingResponse.add(resp);
        }
        return flightBookingResponse;
    }
}
