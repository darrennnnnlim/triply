package com.example.triply.core.booking.dto.flight;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import com.example.triply.core.booking.dto.flight.FlightBookingDTO;
import com.example.triply.core.flight.model.dto.FlightDTO;

public class FlightBookingDTOTest {

    @Test
    public void testGetAndSetId() {
        FlightBookingDTO booking = new FlightBookingDTO();
        booking.setId(1L);
        assertEquals(1L, booking.getId());
    }

    @Test
    public void testGetAndSetFlightId() {
        FlightBookingDTO booking = new FlightBookingDTO();
        booking.setFlightId(100L);
        assertEquals(100L, booking.getFlightId());
    }

    @Test
    public void testGetAndSetFlightClassId() {
        FlightBookingDTO booking = new FlightBookingDTO();
        booking.setFlightClassId(200L);
        assertEquals(200L, booking.getFlightClassId());
    }

    @Test
    public void testGetAndSetBookingId() {
        FlightBookingDTO booking = new FlightBookingDTO();
        booking.setBookingId(300L);
        assertEquals(300L, booking.getBookingId());
    }

    @Test
    public void testGetAndSetUserId() {
        FlightBookingDTO booking = new FlightBookingDTO();
        booking.setUserId(400L);
        assertEquals(400L, booking.getUserId());
    }

    @Test
    public void testGetAndSetDepartureDate() {
        FlightBookingDTO booking = new FlightBookingDTO();
        LocalDateTime departureDate = LocalDateTime.of(2025, 5, 4, 12, 0);
        booking.setDepartureDate(departureDate);
        assertEquals(departureDate, booking.getDepartureDate());
    }

    @Test
    public void testGetAndSetFlight() {
        FlightBookingDTO booking = new FlightBookingDTO();
        FlightDTO flight = new FlightDTO();
        booking.setFlight(flight);
        assertEquals(flight, booking.getFlight());
    }
}