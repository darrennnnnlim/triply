package com.example.triply.core.booking.dto;

import com.example.triply.core.hotel.model.dto.HotelDTO;
import com.example.triply.core.hotel.model.dto.HotelRoomTypeBasicDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

public class HotelBookingDTOTest {

    @Test
    public void testGetAndSetId() {
        HotelBookingDTO booking = new HotelBookingDTO();
        booking.setId(1L);
        assertEquals(1L, booking.getId());
    }

    @Test
    public void testGetAndSetHotelId() {
        HotelBookingDTO booking = new HotelBookingDTO();
        booking.setHotelId(100L);
        assertEquals(100L, booking.getHotelId());
    }

    @Test
    public void testGetAndSetHotelRoomTypeId() {
        HotelBookingDTO booking = new HotelBookingDTO();
        booking.setHotelRoomTypeId(200L);
        assertEquals(200L, booking.getHotelRoomTypeId());
    }

    @Test
    public void testGetAndSetBookingId() {
        HotelBookingDTO booking = new HotelBookingDTO();
        booking.setBookingId(300L);
        assertEquals(300L, booking.getBookingId());
    }

    @Test
    public void testGetAndSetUserId() {
        HotelBookingDTO booking = new HotelBookingDTO();
        booking.setUserId(400L);
        assertEquals(400L, booking.getUserId());
    }

    @Test
    public void testGetAndSetCheckIn() {
        HotelBookingDTO booking = new HotelBookingDTO();
        LocalDateTime checkIn = LocalDateTime.of(2025, 5, 4, 14, 0);
        booking.setCheckIn(checkIn);
        assertEquals(checkIn, booking.getCheckIn());
    }

    @Test
    public void testGetAndSetCheckOut() {
        HotelBookingDTO booking = new HotelBookingDTO();
        LocalDateTime checkOut = LocalDateTime.of(2025, 5, 10, 10, 0);
        booking.setCheckOut(checkOut);
        assertEquals(checkOut, booking.getCheckOut());
    }

    @Test
    public void testGetAndSetHotel() {
        HotelBookingDTO booking = new HotelBookingDTO();
        HotelDTO hotel = new HotelDTO();
        booking.setHotel(hotel);
        assertEquals(hotel, booking.getHotel());
    }

    @Test
    public void testGetAndSetHotelRoomType() {
        HotelBookingDTO booking = new HotelBookingDTO();
        HotelRoomTypeBasicDTO roomType = new HotelRoomTypeBasicDTO();
        booking.setHotelRoomType(roomType);
        assertEquals(roomType, booking.getHotelRoomType());
    }
}