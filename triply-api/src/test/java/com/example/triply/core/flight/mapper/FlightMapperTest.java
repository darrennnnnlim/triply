package com.example.triply.core.flight.mapper;

import com.example.triply.core.flight.model.dto.FlightDTO;
import com.example.triply.core.flight.model.entity.Airline;
import com.example.triply.core.flight.model.entity.Flight;
import com.example.triply.core.flight.repository.AirlineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FlightMapperTest {

    private AirlineRepository airlineRepository;
    private FlightMapper flightMapper;

    @BeforeEach
    void setUp() {
        airlineRepository = mock(AirlineRepository.class);
        flightMapper = new FlightMapper(airlineRepository);
    }

    @Test
    void testToDto_validEntity_returnsDto() {
        Airline airline = new Airline();
        airline.setId(100L);

        Flight flight = new Flight();
        flight.setId(1L);
        flight.setAirline(airline);
        flight.setFlightNumber("TR123");
        flight.setOrigin("SIN");
        flight.setDestination("BKK");
        flight.setDepartureTime(LocalDateTime.now());
        flight.setArrivalTime(LocalDateTime.now().plusHours(2));

        FlightDTO dto = flightMapper.toDto(flight);

        assertNotNull(dto);
        assertEquals(flight.getId(), dto.getId());
        assertEquals(airline.getId(), dto.getAirlineId());
        assertEquals(flight.getFlightNumber(), dto.getFlightNumber());
        assertEquals(flight.getOrigin(), dto.getOrigin());
        assertEquals(flight.getDestination(), dto.getDestination());
        assertEquals(flight.getDepartureTime(), dto.getDepartureTime());
        assertEquals(flight.getArrivalTime(), dto.getArrivalTime());
    }

    @Test
    void testToDto_nullEntity_returnsNull() {
        assertNull(flightMapper.toDto((Flight) null));
    }

    @Test
    void testToEntity_validDto_returnsEntityWithAirline() {
        FlightDTO dto = new FlightDTO();
        dto.setId(1L);
        dto.setAirlineId(100L);
        dto.setFlightNumber("TR123");
        dto.setOrigin("SIN");
        dto.setDestination("BKK");
        dto.setDepartureTime(LocalDateTime.now());
        dto.setArrivalTime(LocalDateTime.now().plusHours(2));

        Airline airline = new Airline();
        airline.setId(100L);

        when(airlineRepository.findById(100L)).thenReturn(Optional.of(airline));

        Flight flight = flightMapper.toEntity(dto);

        assertNotNull(flight);
        assertEquals(dto.getId(), flight.getId());
        assertEquals(dto.getFlightNumber(), flight.getFlightNumber());
        assertEquals(dto.getOrigin(), flight.getOrigin());
        assertEquals(dto.getDestination(), flight.getDestination());
        assertEquals(dto.getDepartureTime(), flight.getDepartureTime());
        assertEquals(dto.getArrivalTime(), flight.getArrivalTime());
        assertEquals(airline, flight.getAirline());
    }

    @Test
    void testToEntity_airlineNotFound_setsAirlineToNull() {
        FlightDTO dto = new FlightDTO();
        dto.setAirlineId(404L);

        when(airlineRepository.findById(404L)).thenReturn(Optional.empty());

        Flight flight = flightMapper.toEntity(dto);
        assertNull(flight.getAirline());
    }

    @Test
    void testToEntity_nullDto_returnsNull() {
        assertNull(flightMapper.toEntity((FlightDTO) null));
    }
}
