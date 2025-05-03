package com.example.triply.core.flight.mapper;

import com.example.triply.core.flight.model.dto.AirlineDTO;
import com.example.triply.core.flight.model.entity.Airline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AirlineMapperTest {

    private AirlineMapper airlineMapper;

    @BeforeEach
    void setUp() {
        airlineMapper = new AirlineMapper();
    }

    @Test
    void testToDto_validEntity_returnsDto() {
        Airline airline = new Airline();
        airline.setId(1L);
        airline.setName("Singapore Airlines");
        airline.setCode("SQ");

        AirlineDTO dto = airlineMapper.toDto(airline);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Singapore Airlines", dto.getName());
        assertEquals("SQ", dto.getCode());
    }

    @Test
    void testToDto_nullEntity_returnsNull() {
        assertNull(airlineMapper.toDto((Airline) null));
    }

    @Test
    void testToEntity_validDto_returnsEntity() {
        AirlineDTO dto = new AirlineDTO();
        dto.setId(2L);
        dto.setName("Scoot");
        dto.setCode("TR");

        Airline airline = airlineMapper.toEntity(dto);

        assertNotNull(airline);
        assertEquals(2L, airline.getId());
        assertEquals("Scoot", airline.getName());
        assertEquals("TR", airline.getCode());
    }

    @Test
    void testToEntity_nullDto_returnsNull() {
        assertNull(airlineMapper.toEntity((AirlineDTO) null));
    }
}
