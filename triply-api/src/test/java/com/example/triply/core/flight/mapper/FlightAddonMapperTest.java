package com.example.triply.core.flight.mapper;

import com.example.triply.core.flight.model.dto.FlightAddonDTO;
import com.example.triply.core.flight.model.entity.FlightAddon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FlightAddonMapperTest {

    private FlightAddonMapper flightAddonMapper;

    @BeforeEach
    void setUp() {
        flightAddonMapper = new FlightAddonMapper();
    }

    @Test
    void testToDto_validEntity_returnsDto() {
        FlightAddon addon = new FlightAddon();
        addon.setId(1L);
        addon.setName("Extra Legroom");

        FlightAddonDTO dto = flightAddonMapper.toDto(addon);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Extra Legroom", dto.getName());
    }

    @Test
    void testToDto_nullEntity_returnsNull() {
        assertNull(flightAddonMapper.toDto((FlightAddon) null));
    }

    @Test
    void testToEntity_validDto_returnsEntity() {
        FlightAddonDTO dto = new FlightAddonDTO();
        dto.setId(2L);
        dto.setName("Meal Upgrade");

        FlightAddon addon = flightAddonMapper.toEntity(dto);

        assertNotNull(addon);
        assertEquals(2L, addon.getId());
        assertEquals("Meal Upgrade", addon.getName());
    }

    @Test
    void testToEntity_nullDto_returnsNull() {
        assertNull(flightAddonMapper.toEntity((FlightAddonDTO) null));
    }
}
