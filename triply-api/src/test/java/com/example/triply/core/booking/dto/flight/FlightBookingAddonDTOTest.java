package com.example.triply.core.booking.dto.flight;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import com.example.triply.core.flight.model.dto.FlightAddonDTO;

public class FlightBookingAddonDTOTest {

    @Test
    public void testGetAndSetId() {
        FlightBookingAddonDTO addon = new FlightBookingAddonDTO();
        addon.setId(1L);
        assertEquals(1L, addon.getId());
    }

    @Test
    public void testGetAndSetFlightBookingId() {
        FlightBookingAddonDTO addon = new FlightBookingAddonDTO();
        addon.setFlightBookingId(100L);
        assertEquals(100L, addon.getFlightBookingId());
    }

    @Test
    public void testGetAndSetFlightAddonId() {
        FlightBookingAddonDTO addon = new FlightBookingAddonDTO();
        addon.setFlightAddonId(200L);
        assertEquals(200L, addon.getFlightAddonId());
    }

    @Test
    public void testGetAndSetPrice() {
        FlightBookingAddonDTO addon = new FlightBookingAddonDTO();
        BigDecimal price = new BigDecimal("49.99");
        addon.setPrice(price);
        assertEquals(price, addon.getPrice());
    }

    @Test
    public void testGetAndSetQuantity() {
        FlightBookingAddonDTO addon = new FlightBookingAddonDTO();
        int quantity = 2;
        addon.setQuantity(quantity);
        assertEquals(quantity, addon.getQuantity());
    }

    @Test
    public void testGetAndSetFlightAddon() {
        FlightBookingAddonDTO addon = new FlightBookingAddonDTO();
        FlightAddonDTO flightAddon = new FlightAddonDTO();
        addon.setFlightAddon(flightAddon);
        assertEquals(flightAddon, addon.getFlightAddon());
    }
}