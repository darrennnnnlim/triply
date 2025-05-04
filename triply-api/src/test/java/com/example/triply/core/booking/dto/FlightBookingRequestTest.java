package com.example.triply.core.booking.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FlightBookingRequestTest {

    @Test
    void testSettersAndGetters() {
        FlightBookingRequest request = new FlightBookingRequest();

        Long userId = 1L;
        Long flightId = 101L;
        String seatClass = "ECONOMY";
        String seatNumber = "22B";

        request.setUserId(userId);
        request.setFlightId(flightId);
        request.setSeatClass(seatClass);
        request.setSeatNumber(seatNumber);

        assertEquals(userId, request.getUserId());
        assertEquals(flightId, request.getFlightId());
        assertEquals(seatClass, request.getSeatClass());
        assertEquals(seatNumber, request.getSeatNumber());
    }
}
