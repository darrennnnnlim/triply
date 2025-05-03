package com.example.triply.core.booking.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FlightBookingResponseTest {

    @Test
    public void testGetAndSetUserId() {
        FlightBookingResponse response = new FlightBookingResponse();
        response.setUserId(100L);
        assertEquals(100L, response.getUserId());
    }

    @Test
    public void testGetAndSetFlightId() {
        FlightBookingResponse response = new FlightBookingResponse();
        response.setFlightId(200L);
        assertEquals(200L, response.getFlightId());
    }

    @Test
    public void testGetAndSetBookingId() {
        FlightBookingResponse response = new FlightBookingResponse();
        response.setBookingId(300L);
        assertEquals(300L, response.getBookingId());
    }

    @Test
    public void testGetAndSetDepartureDate() {
        FlightBookingResponse response = new FlightBookingResponse();
        String departureDate = "2025-05-04T12:00:00";
        response.setDepartureDate(departureDate);
        assertEquals(departureDate, response.getDepartureDate());
    }
}