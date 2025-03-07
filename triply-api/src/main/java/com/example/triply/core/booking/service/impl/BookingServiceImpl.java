package com.example.triply.core.booking.service.impl;

import com.example.triply.core.booking.dto.BookingDTO;
import com.example.triply.core.booking.entity.Booking;
import com.example.triply.core.booking.entity.BookingStatusEnum;
import com.example.triply.core.booking.repository.BookingRepository;
import com.example.triply.core.booking.service.BookingService;
import com.example.triply.core.booking.service.template.FlightBookingService;
import com.example.triply.core.booking.service.template.HotelBookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookingServiceImpl implements BookingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final FlightBookingService flightBookingService;
    private final HotelBookingService hotelBookingService;
    private final BookingRepository bookingRepository;

    public BookingServiceImpl(FlightBookingService flightBookingService, HotelBookingService hotelBookingService, BookingRepository bookingRepository) {
        this.flightBookingService = flightBookingService;
        this.hotelBookingService = hotelBookingService;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public BookingDTO processBooking(BookingDTO bookingDTO) {
        try {

            // If both Flight Booking and Hotel Booking are null, then why do booking?
            if (bookingDTO.getFlightBooking() == null && bookingDTO.getHotelBooking() == null) {

            }

            if (bookingDTO.getFlightBooking() != null) {
                flightBookingService.processBooking(bookingDTO);
            }

            if (bookingDTO.getHotelBooking() != null) {
                hotelBookingService.processBooking(bookingDTO);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Process Booking Bad Request");
        }
        return null;
    }
}
