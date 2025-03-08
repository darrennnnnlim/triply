package com.example.triply.core.booking.service;

import com.example.triply.core.booking.dto.BookingDTO;

import java.util.List;

public interface BookingService {

    BookingDTO processBooking(BookingDTO type);

    List<BookingDTO> getBooking(String username);
}
