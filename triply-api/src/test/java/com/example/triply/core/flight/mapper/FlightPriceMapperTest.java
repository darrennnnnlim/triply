package com.example.triply.core.flight.mapper;

import com.example.triply.core.flight.model.dto.FlightClassDTO;
import com.example.triply.core.flight.model.dto.FlightDTO;
import com.example.triply.core.flight.model.dto.FlightPriceDTO;
import com.example.triply.core.flight.model.entity.Flight;
import com.example.triply.core.flight.model.entity.FlightClass;
import com.example.triply.core.flight.model.entity.FlightPrice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FlightPriceMapperTest {

    private FlightPriceMapper flightPriceMapper;
    private FlightMapper flightMapper;
    private FlightClassMapper flightClassMapper;

    @BeforeEach
    void setUp() {
        flightClassMapper = mock(FlightClassMapper.class);
        flightMapper = mock(FlightMapper.class);
        flightPriceMapper = new FlightPriceMapper(flightMapper, flightClassMapper);
    }

    @Test
    void testToDto_validEntity_returnsDto() {
        LocalDateTime departureDate = LocalDateTime.of(2025,5,1,0,0,0);

        Flight flight = new Flight();
        flight.setFlightNumber("TR123");
        flight.setOrigin("SIN");
        flight.setDestination("BKK");

        FlightClass flightClass = new FlightClass();
        flightClass.setClassName("Economy");

        FlightDTO flightDTO = new FlightDTO();
        flightDTO.setFlightNumber("TR123");
        flightDTO.setOrigin("SIN");
        flightDTO.setDestination("BKK");

        FlightClassDTO flightClassDTO = new FlightClassDTO();
        flightClassDTO.setClassName("Economy");

        FlightPrice price = new FlightPrice();
        price.setId(1L);
        price.setDepartureDate(departureDate);
        price.setBasePrice(BigDecimal.valueOf(200));
        price.setDiscount(BigDecimal.valueOf(20));
        price.setSurgeMultiplier(BigDecimal.valueOf(1.2));
        price.setFlight(flight);
        price.setFlightClass(flightClass);

        when(flightMapper.toDto(any(Flight.class))).thenReturn(flightDTO);
        when(flightClassMapper.toDto(any(FlightClass.class))).thenReturn(flightClassDTO);

        FlightPriceDTO dto = flightPriceMapper.toDto(price);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(departureDate, dto.getDepartureDate());
        assertEquals(BigDecimal.valueOf(200), dto.getBasePrice());
        assertEquals(BigDecimal.valueOf(20), dto.getDiscount());
        assertEquals(BigDecimal.valueOf(1.2), dto.getSurgeMultiplier());

        assertEquals("TR123", dto.getFlightNumber());
        assertEquals("SIN", dto.getOrigin());
        assertEquals("BKK", dto.getDestination());
        assertEquals("Economy", dto.getFlightClassName());

        assertSame(flightDTO, dto.getFlightDTO());
        assertSame(flightClassDTO, dto.getFlightClassDTO());
    }

    @Test
    void testToDto_nullEntity_returnsNull() {
        assertNull(flightPriceMapper.toDto((FlightPrice) null));
    }

    @Test
    void testToDto_missingRelatedEntities_handlesGracefully() {
        LocalDateTime departureDate = LocalDateTime.of(2025,5,1,0,0,0);

        FlightPrice price = new FlightPrice();
        price.setId(2L);
        price.setBasePrice(BigDecimal.valueOf(150));
        price.setDiscount(BigDecimal.ZERO);
        price.setSurgeMultiplier(BigDecimal.ONE);
        price.setDepartureDate(departureDate);
        price.setFlight(null);
        price.setFlightClass(null);

        FlightPriceDTO dto = flightPriceMapper.toDto(price);

        assertNotNull(dto);
        assertEquals(2L, dto.getId());
        assertEquals(BigDecimal.valueOf(150), dto.getBasePrice());
        assertEquals(BigDecimal.ZERO, dto.getDiscount());
        assertEquals(BigDecimal.ONE, dto.getSurgeMultiplier());
        assertEquals(departureDate, dto.getDepartureDate());

        assertNull(dto.getFlightNumber());
        assertNull(dto.getOrigin());
        assertNull(dto.getDestination());
        assertNull(dto.getFlightClassName());

        assertNull(dto.getFlightDTO());
        assertNull(dto.getFlightClassDTO());
    }

    @Test
    void testToEntity_returnsNull() {
        assertNull(flightPriceMapper.toEntity(new FlightPriceDTO()));
    }

    @Test
    void testToDtoList_returnsEmptyList() {
        assertTrue(flightPriceMapper.toDto(java.util.List.of()).isEmpty());
    }

    @Test
    void testToEntityList_returnsEmptyList() {
        assertTrue(flightPriceMapper.toEntity(java.util.List.of()).isEmpty());
    }
}
