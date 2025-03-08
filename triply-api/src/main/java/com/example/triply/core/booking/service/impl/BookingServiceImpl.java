package com.example.triply.core.booking.service.impl;

import com.example.triply.core.booking.dto.BookingDTO;
import com.example.triply.core.booking.service.BookingService;
import com.example.triply.core.booking.service.template.FlightBookingService;
import com.example.triply.core.booking.service.template.HotelBookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BookingServiceImpl implements BookingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final FlightBookingService flightBookingService;
    private final HotelBookingService hotelBookingService;

    public BookingServiceImpl(FlightBookingService flightBookingService, HotelBookingService hotelBookingService) {
        this.flightBookingService = flightBookingService;
        this.hotelBookingService = hotelBookingService;
    }

    @Override
    public BookingDTO processBooking(BookingDTO bookingDTO) {
        try {

            // If both Flight Booking and Hotel Booking are null, then why do booking?
            if (bookingDTO.getFlightBooking() == null && bookingDTO.getHotelBooking() == null) {
                LOGGER.error("Invalid request: Flight Booking and Hotel Booking details are required");
                throw new IllegalArgumentException("Invalid request: Flight Booking and Hotel Booking details are required");
            }

            if (bookingDTO.getFlightBooking() != null) {
                flightBookingService.processBooking(bookingDTO);
            }

            if (bookingDTO.getHotelBooking() != null) {
                hotelBookingService.processBooking(bookingDTO);
            }

            return bookingDTO;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Process Booking Bad Request");
        }
    }
}
