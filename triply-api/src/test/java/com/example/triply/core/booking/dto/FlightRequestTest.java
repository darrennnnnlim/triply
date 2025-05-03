package com.example.triply.core.booking.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FlightRequestTest {

    @Test
    public void testGetAndSetId() {
        FlightRequest request = new FlightRequest();
        request.setId(1L);
        assertEquals(1L, request.getId());
    }

    @Test
    public void testGetAndSetFlightNumber() {
        FlightRequest request = new FlightRequest();
        request.setFlightNumber("AB123");
        assertEquals("AB123", request.getFlightNumber());
    }

    @Test
    public void testGetAndSetAirline() {
        FlightRequest request = new FlightRequest();
        request.setAirline("AirlineName");
        assertEquals("AirlineName", request.getAirline());
    }

    @Test
    public void testGetAndSetDeparture() {
        FlightRequest request = new FlightRequest();
        request.setDeparture("CityA");
        assertEquals("CityA", request.getDeparture());
    }

    @Test
    public void testGetAndSetArrival() {
        FlightRequest request = new FlightRequest();
        request.setArrival("CityB");
        assertEquals("CityB", request.getArrival());
    }

    @Test
    public void testGetAndSetDepartureTime() {
        FlightRequest request = new FlightRequest();
        LocalDateTime departureTime = LocalDateTime.now();
        request.setDepartureTime(departureTime);
        assertEquals(departureTime, request.getDepartureTime());
    }

    @Test
    public void testGetAndSetArrivalTime() {
        FlightRequest request = new FlightRequest();
        LocalDateTime arrivalTime = LocalDateTime.now().plusHours(5);
        request.setArrivalTime(arrivalTime);
        assertEquals(arrivalTime, request.getArrivalTime());
    }

    @Test
    public void testGetAndSetBasePrice() {
        FlightRequest request = new FlightRequest();
        BigDecimal basePrice = new BigDecimal("199.99");
        request.setBasePrice(basePrice);
        assertEquals(basePrice, request.getBasePrice());
    }
}