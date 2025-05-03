package com.example.triply.core.booking.mapper.flight;

import com.example.triply.core.booking.dto.flight.FlightBookingAddonDTO;
import com.example.triply.core.booking.entity.flight.FlightBooking;
import com.example.triply.core.booking.entity.flight.FlightBookingAddon;
import com.example.triply.core.booking.repository.flight.FlightBookingRepository;
import com.example.triply.core.flight.model.entity.FlightAddon;
import com.example.triply.core.flight.repository.FlightAddonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FlightBookingAddonMapperTest {

    private FlightBookingRepository flightBookingRepository;
    private FlightAddonRepository flightAddonRepository;
    private FlightBookingAddonMapper mapper;

    @BeforeEach
    void setUp() {
        flightBookingRepository = mock(FlightBookingRepository.class);
        flightAddonRepository = mock(FlightAddonRepository.class);
        mapper = new FlightBookingAddonMapper(flightBookingRepository, flightAddonRepository);
    }

    @Test
    void testToDto_validEntity_returnsDto() {
        FlightBooking booking = new FlightBooking();
        booking.setId(100L);

        FlightAddon addon = new FlightAddon();
        addon.setId(200L);

        FlightBookingAddon entity = new FlightBookingAddon();
        entity.setId(1L);
        entity.setFlightBooking(booking);
        entity.setFlightAddon(addon);
        entity.setPrice(BigDecimal.valueOf(50));
        entity.setQuantity(2);

        FlightBookingAddonDTO dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(100L, dto.getFlightBookingId());
        assertEquals(200L, dto.getFlightAddonId());
        assertEquals(BigDecimal.valueOf(50), dto.getPrice());
        assertEquals(2, dto.getQuantity());
    }

    @Test
    void testToDto_nullEntity_returnsNull() {
        assertNull(mapper.toDto((FlightBookingAddon) null));
    }

    @Test
    void testToEntity_validDto_returnsEntityWithRefs() {
        FlightBookingAddonDTO dto = new FlightBookingAddonDTO();
        dto.setId(10L);
        dto.setFlightBookingId(111L);
        dto.setFlightAddonId(222L);
        dto.setPrice(BigDecimal.valueOf(99.99));
        dto.setQuantity(3);

        FlightBooking booking = new FlightBooking();
        booking.setId(111L);
        when(flightBookingRepository.findById(111L)).thenReturn(Optional.of(booking));

        FlightAddon addon = new FlightAddon();
        addon.setId(222L);
        when(flightAddonRepository.findFlightAddonById(222L)).thenReturn(Optional.of(addon));

        FlightBookingAddon entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(10L, entity.getId());
        assertEquals(booking, entity.getFlightBooking());
        assertEquals(addon, entity.getFlightAddon());
        assertEquals(BigDecimal.valueOf(99.99), entity.getPrice());
        assertEquals(3, entity.getQuantity());
    }

    @Test
    void testToEntity_missingReferences_setsNulls() {
        FlightBookingAddonDTO dto = new FlightBookingAddonDTO();
        dto.setId(10L);
        dto.setFlightBookingId(999L);
        dto.setFlightAddonId(888L);
        dto.setPrice(BigDecimal.TEN);
        dto.setQuantity(1);

        when(flightBookingRepository.findById(999L)).thenReturn(Optional.empty());
        when(flightAddonRepository.findFlightAddonById(888L)).thenReturn(Optional.empty());

        FlightBookingAddon entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(10L, entity.getId());
        assertNull(entity.getFlightBooking());
        assertNull(entity.getFlightAddon());
        assertEquals(BigDecimal.TEN, entity.getPrice());
        assertEquals(1, entity.getQuantity());
    }

    @Test
    void testToEntity_nullDto_returnsNull() {
        assertNull(mapper.toEntity((FlightBookingAddonDTO) null));
    }
}
