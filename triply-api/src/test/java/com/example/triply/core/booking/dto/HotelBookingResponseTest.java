package com.example.triply.core.booking.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HotelBookingResponseTest {

    @Test
    public void testGetAndSetUserId() {
        HotelBookingResponse response = new HotelBookingResponse();
        response.setUserId(100L);
        assertEquals(100L, response.getUserId());
    }

    @Test
    public void testGetAndSetHotelId() {
        HotelBookingResponse response = new HotelBookingResponse();
        response.setHotelId(200L);
        assertEquals(200L, response.getHotelId());
    }

    @Test
    public void testGetAndSetCheckIn() {
        HotelBookingResponse response = new HotelBookingResponse();
        String checkIn = "2025-05-04T14:00:00";
        response.setCheckIn(checkIn);
        assertEquals(checkIn, response.getCheckIn());
    }

    @Test
    public void testGetAndSetCheckOut() {
        HotelBookingResponse response = new HotelBookingResponse();
        String checkOut = "2025-05-10T10:00:00";
        response.setCheckOut(checkOut);
        assertEquals(checkOut, response.getCheckOut());
    }

    @Test
    public void testGetAndSetRoomType() {
        HotelBookingResponse response = new HotelBookingResponse();
        String roomType = "Deluxe";
        response.setRoomType(roomType);
        assertEquals(roomType, response.getRoomType());
    }
}