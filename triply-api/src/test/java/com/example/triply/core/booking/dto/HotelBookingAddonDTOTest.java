package com.example.triply.core.booking.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import com.example.triply.core.hotel.model.dto.HotelAddonDTO;

public class HotelBookingAddonDTOTest {

    @Test
    public void testGetAndSetId() {
        HotelBookingAddonDTO addon = new HotelBookingAddonDTO();
        addon.setId(1L);
        assertEquals(1L, addon.getId());
    }

    @Test
    public void testGetAndSetHotelBookingId() {
        HotelBookingAddonDTO addon = new HotelBookingAddonDTO();
        addon.setHotelBookingId(100L);
        assertEquals(100L, addon.getHotelBookingId());
    }

    @Test
    public void testGetAndSetHotelAddonId() {
        HotelBookingAddonDTO addon = new HotelBookingAddonDTO();
        addon.setHotelAddonId(200L);
        assertEquals(200L, addon.getHotelAddonId());
    }

    @Test
    public void testGetAndSetQuantity() {
        HotelBookingAddonDTO addon = new HotelBookingAddonDTO();
        addon.setQuantity(2);
        assertEquals(2, addon.getQuantity());
    }

    @Test
    public void testGetAndSetTotalPrice() {
        HotelBookingAddonDTO addon = new HotelBookingAddonDTO();
        BigDecimal price = new BigDecimal("49.99");
        addon.setTotalPrice(price);
        assertEquals(price, addon.getTotalPrice());
    }

    @Test
    public void testGetAndSetHotelAddon() {
        HotelBookingAddonDTO addon = new HotelBookingAddonDTO();
        HotelAddonDTO hotelAddon = new HotelAddonDTO();
        addon.setHotelAddon(hotelAddon);
        assertEquals(hotelAddon, addon.getHotelAddon());
    }
}