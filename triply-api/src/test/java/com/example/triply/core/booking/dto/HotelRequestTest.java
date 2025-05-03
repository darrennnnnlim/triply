package com.example.triply.core.booking.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;

public class HotelRequestTest {

    @Test
    public void testGetAndSetId() {
        HotelRequest request = new HotelRequest();
        request.setId(1L);
        assertEquals(1L, request.getId());
    }

    @Test
    public void testGetAndSetName() {
        HotelRequest request = new HotelRequest();
        String name = "Grand Hotel";
        request.setName(name);
        assertEquals(name, request.getName());
    }

    @Test
    public void testGetAndSetLocation() {
        HotelRequest request = new HotelRequest();
        String location = "New York";
        request.setLocation(location);
        assertEquals(location, request.getLocation());
    }

    @Test
    public void testGetAndSetAvailability() {
        HotelRequest request = new HotelRequest();
        boolean availability = true;
        request.setAvailability(availability);
        assertEquals(availability, request.isAvailability());
    }

    @Test
    public void testGetAndSetBasePrice() {
        HotelRequest request = new HotelRequest();
        BigDecimal price = new BigDecimal("149.99");
        request.setBasePrice(price);
        assertEquals(price, request.getBasePrice());
    }
}