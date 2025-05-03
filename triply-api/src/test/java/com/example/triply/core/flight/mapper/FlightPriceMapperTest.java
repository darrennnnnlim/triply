package com.example.triply.core.flight.mapper;

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

class FlightPriceMapperTest {

    private FlightPriceMapper flightPriceMapper;

    @BeforeEach
    void setUp() {
        flightPriceMapper = new FlightPriceMapper();
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

        FlightPrice price = new FlightPrice();
        price.setId(1L);
        price.setDepartureDate(departureDate);
        price.setBasePrice(BigDecimal.valueOf(200));
        price.setDiscount(BigDecimal.valueOf(20));
        price.setSurgeMultiplier(BigDecimal.valueOf(1.2));
        price.setFlight(flight);
        price.setFlightClass(flightClass);

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

        assertSame(flight, dto.getFlight());
        assertSame(flightClass, dto.getFlightClass());
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

        assertNull(dto.getFlight());
        assertNull(dto.getFlightClass());
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
