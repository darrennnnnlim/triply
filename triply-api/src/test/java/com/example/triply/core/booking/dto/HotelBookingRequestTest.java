package com.example.triply.core.booking.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class HotelBookingRequestTest {

    @Test
    public void testGetAndSetHotelId() {
        HotelBookingRequest request = new HotelBookingRequest();
        request.setHotelId(100L);
        assertEquals(100L, request.getHotelId());
    }

    @Test
    public void testGetAndSetCheckInDate() {
        HotelBookingRequest request = new HotelBookingRequest();
        LocalDate checkInDate = LocalDate.of(2025, 5, 4);
        request.setCheckInDate(checkInDate);
        assertEquals(checkInDate, request.getCheckInDate());
    }

    @Test
    public void testGetAndSetCheckOutDate() {
        HotelBookingRequest request = new HotelBookingRequest();
        LocalDate checkOutDate = LocalDate.of(2025, 5, 10);
        request.setCheckOutDate(checkOutDate);
        assertEquals(checkOutDate, request.getCheckOutDate());
    }

    @Test
    public void testGetAndSetRoomType() {
        HotelBookingRequest request = new HotelBookingRequest();
        String roomType = "Deluxe";
        request.setRoomType(roomType);
        assertEquals(roomType, request.getRoomType());
    }
}