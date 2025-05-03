package com.example.triply.core.booking.dto;

import com.example.triply.core.booking.dto.flight.FlightBookingAddonDTO;
import com.example.triply.core.booking.dto.flight.FlightBookingDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

public class BookingDTOTest {

    @Test
    public void testGetAndSetId() {
        BookingDTO booking = new BookingDTO();
        booking.setId(1L);
        assertEquals(1L, booking.getId());
    }

    @Test
    public void testGetAndSetUserId() {
        BookingDTO booking = new BookingDTO();
        booking.setUserId(100L);
        assertEquals(100L, booking.getUserId());
    }

    @Test
    public void testGetAndSetFinalPrice() {
        BookingDTO booking = new BookingDTO();
        BigDecimal price = new BigDecimal("299.99");
        booking.setFinalPrice(price);
        assertEquals(price, booking.getFinalPrice());
    }

    @Test
    public void testGetAndSetStatus() {
        BookingDTO booking = new BookingDTO();
        String status = "CONFIRMED";
        booking.setStatus(status);
        assertEquals(status, booking.getStatus());
    }

    @Test
    public void testGetAndSetBookingTime() {
        BookingDTO booking = new BookingDTO();
        LocalDateTime bookingTime = LocalDateTime.now();
        booking.setBookingTime(bookingTime);
        assertEquals(bookingTime, booking.getBookingTime());
    }

    @Test
    public void testGetAndSetFlightBooking() {
        BookingDTO booking = new BookingDTO();
        FlightBookingDTO flightBooking = new FlightBookingDTO();
        booking.setFlightBooking(flightBooking);
        assertEquals(flightBooking, booking.getFlightBooking());
    }

    @Test
    public void testGetAndSetHotelBooking() {
        BookingDTO booking = new BookingDTO();
        HotelBookingDTO hotelBooking = new HotelBookingDTO();
        booking.setHotelBooking(hotelBooking);
        assertEquals(hotelBooking, booking.getHotelBooking());
    }

    @Test
    public void testGetAndSetFlightBookingAddon() {
        BookingDTO booking = new BookingDTO();
        FlightBookingAddonDTO addon = new FlightBookingAddonDTO();
        booking.setFlightBookingAddon(Arrays.asList(addon));
        assertEquals(Arrays.asList(addon), booking.getFlightBookingAddon());
    }

    @Test
    public void testGetAndSetHotelBookingAddon() {
        BookingDTO booking = new BookingDTO();
        HotelBookingAddonDTO addon = new HotelBookingAddonDTO();
        booking.setHotelBookingAddon(Arrays.asList(addon));
        assertEquals(Arrays.asList(addon), booking.getHotelBookingAddon());
    }
}