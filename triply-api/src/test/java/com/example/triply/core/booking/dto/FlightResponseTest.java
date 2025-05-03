package com.example.triply.core.booking.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FlightResponseTest {

    @Test
    public void testGetAndSetId() {
        FlightResponse response = new FlightResponse();
        response.setId(1L);
        assertEquals(1L, response.getId());
    }

    @Test
    public void testGetAndSetFlightNumber() {
        FlightResponse response = new FlightResponse();
        response.setFlightNumber("AB123");
        assertEquals("AB123", response.getFlightNumber());
    }

    @Test
    public void testGetAndSetAirline() {
        FlightResponse response = new FlightResponse();
        response.setAirline("AirlineName");
        assertEquals("AirlineName", response.getAirline());
    }

    @Test
    public void testGetAndSetDepartureTime() {
        FlightResponse response = new FlightResponse();
        LocalDateTime departureTime = LocalDateTime.of(2025, 5, 4, 12, 0);
        response.setDepartureTime(departureTime);
        assertEquals(departureTime, response.getDepartureTime());
    }

    @Test
    public void testGetAndSetArrivalTime() {
        FlightResponse response = new FlightResponse();
        LocalDateTime arrivalTime = LocalDateTime.of(2025, 5, 4, 17, 0);
        response.setArrivalTime(arrivalTime);
        assertEquals(arrivalTime, response.getArrivalTime());
    }
}