package com.example.triply.core.booking.mapper.hotel;

import com.example.triply.core.booking.dto.HotelBookingAddonDTO;
import com.example.triply.core.booking.entity.hotel.HotelBooking;
import com.example.triply.core.booking.entity.hotel.HotelBookingAddon;
import com.example.triply.core.booking.repository.hotel.HotelBookingRepository;
import com.example.triply.core.hotel.model.entity.HotelAddon;
import com.example.triply.core.hotel.repository.HotelAddonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HotelBookingAddonMapperTest {

    private HotelBookingRepository hotelBookingRepository;
    private HotelAddonRepository hotelAddonRepository;
    private HotelBookingAddonMapper mapper;

    @BeforeEach
    void setUp() {
        hotelBookingRepository = mock(HotelBookingRepository.class);
        hotelAddonRepository = mock(HotelAddonRepository.class);
        mapper = new HotelBookingAddonMapper(hotelBookingRepository, hotelAddonRepository);
    }

    @Test
    void testToDto_validEntity_returnsDto() {
        HotelBooking booking = new HotelBooking();
        booking.setId(100L);

        HotelAddon addon = new HotelAddon();
        addon.setId(200L);

        HotelBookingAddon entity = new HotelBookingAddon();
        entity.setId(1L);
        entity.setHotelBooking(booking);
        entity.setHotelAddon(addon);
        entity.setQuantity(2);
        entity.setTotalPrice(BigDecimal.valueOf(88.88));

        HotelBookingAddonDTO dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(100L, dto.getHotelBookingId());
        assertEquals(200L, dto.getHotelAddonId());
        assertEquals(2, dto.getQuantity());
        assertEquals(BigDecimal.valueOf(88.88), dto.getTotalPrice());
    }

    @Test
    void testToDto_nullEntity_returnsNull() {
        assertNull(mapper.toDto((HotelBookingAddon) null));
    }

    @Test
    void testToEntity_validDto_returnsEntityWithRefs() {
        HotelBookingAddonDTO dto = new HotelBookingAddonDTO();
        dto.setId(10L);
        dto.setHotelBookingId(111L);
        dto.setHotelAddonId(222L);
        dto.setQuantity(3);
        dto.setTotalPrice(BigDecimal.valueOf(199.99));

        HotelBooking booking = new HotelBooking();
        booking.setId(111L);

        HotelAddon addon = new HotelAddon();
        addon.setId(222L);

        when(hotelBookingRepository.findById(111L)).thenReturn(Optional.of(booking));
        when(hotelAddonRepository.findById(222L)).thenReturn(Optional.of(addon));

        HotelBookingAddon entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(10L, entity.getId());
        assertEquals(booking, entity.getHotelBooking());
        assertEquals(addon, entity.getHotelAddon());
        assertEquals(3, entity.getQuantity());
        assertEquals(BigDecimal.valueOf(199.99), entity.getTotalPrice());
    }

    @Test
    void testToEntity_missingReferences_setsNulls() {
        HotelBookingAddonDTO dto = new HotelBookingAddonDTO();
        dto.setId(20L);
        dto.setHotelBookingId(999L);
        dto.setHotelAddonId(888L);
        dto.setQuantity(1);
        dto.setTotalPrice(BigDecimal.TEN);

        when(hotelBookingRepository.findById(999L)).thenReturn(Optional.empty());
        when(hotelAddonRepository.findById(888L)).thenReturn(Optional.empty());

        HotelBookingAddon entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(20L, entity.getId());
        assertNull(entity.getHotelBooking());
        assertNull(entity.getHotelAddon());
        assertEquals(1, entity.getQuantity());
        assertEquals(BigDecimal.TEN, entity.getTotalPrice());
    }

    @Test
    void testToEntity_nullDto_returnsNull() {
        assertNull(mapper.toEntity((HotelBookingAddonDTO) null));
    }
}
