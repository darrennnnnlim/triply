package com.example.triply.core.booking.service;

import com.example.triply.core.booking.dto.BookingDTO;
import com.example.triply.core.booking.entity.Booking;

public interface BookingService {

    public BookingDTO processBooking(BookingDTO type);
}
