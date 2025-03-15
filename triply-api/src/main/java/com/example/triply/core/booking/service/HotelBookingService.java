package com.example.triply.core.booking.service;


import com.example.triply.core.booking.dto.FlightBookingResponse;
import com.example.triply.core.booking.dto.HotelBookingRequest;
import com.example.triply.core.booking.dto.HotelBookingResponse;
import com.example.triply.core.booking.entity.Booking;
import com.example.triply.core.booking.entity.flight.FlightBooking;
import com.example.triply.core.booking.entity.hotel.HotelBooking;
import com.example.triply.core.booking.repository.FlightBookingRepository;
import com.example.triply.core.booking.repository.HotelBookingRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class HotelBookingService extends AbstractBookingService<HotelBookingRequest> {

    @Autowired
    private HotelBookingRepository hotelBookingRepository;

    @Override
    protected void validateRequest(HotelBookingRequest request) {

    }

    @Override
    protected void checkAvailability(HotelBookingRequest request) {

    }

    @Override
    protected BigDecimal calculateTotalPrice(HotelBookingRequest request) {
        return null;
    }

    @Override
    protected Booking createBooking(HotelBookingRequest request, BigDecimal price) {
        return null;
    }

    @Override
    protected void confirmBooking(Booking booking) {

    }
    public List<HotelBookingResponse> getBookingByUserId (Long userId){
        List<HotelBooking> hotelBookings = hotelBookingRepository.findByUserId(userId);
        List<HotelBookingResponse> hotelBookingResponses = new ArrayList<>();
        for (HotelBooking booking : hotelBookings) {
            HotelBookingResponse resp = new HotelBookingResponse();
            resp.setUserId(userId);
            resp.setHotelId(booking.getHotel().getId());
            resp.setCheckInDate(booking.getCheckInDate());
            resp.setCheckOutDate(booking.getCheckOutDate());
            resp.setRoomType(booking.getRoomType());
            hotelBookingResponses.add(resp);
        }
        return hotelBookingResponses;
    }

    public List<HotelBookingResponse> getBookingByBookingId (Long bookingId, Long userId){
        List<HotelBooking> hotelBookings = hotelBookingRepository.findByBookingId(userId);
        List<HotelBookingResponse> hotelBookingResponses = new ArrayList<>();
        for (HotelBooking booking : hotelBookings) {
            HotelBookingResponse resp = new HotelBookingResponse();
            resp.setUserId(userId);
            resp.setHotelId(booking.getHotel().getId());
            resp.setCheckInDate(booking.getCheckInDate());
            resp.setCheckOutDate(booking.getCheckOutDate());
            resp.setRoomType(booking.getRoomType());
            hotelBookingResponses.add(resp);
        }
        return hotelBookingResponses;
    }

}
