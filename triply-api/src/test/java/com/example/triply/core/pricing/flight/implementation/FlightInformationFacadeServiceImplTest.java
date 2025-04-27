package com.example.triply.core.pricing.flight.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.triply.core.flight.model.entity.Airline;
import com.example.triply.core.flight.model.entity.Flight;
import com.example.triply.core.flight.model.entity.FlightClass;
import com.example.triply.core.flight.model.entity.FlightPrice;
import com.example.triply.core.flight.repository.AirlineRepository;
import com.example.triply.core.flight.repository.FlightClassRepository;
import com.example.triply.core.flight.repository.FlightPriceRepository;
import com.example.triply.core.flight.repository.FlightRepository;
import com.example.triply.core.pricing.flight.model.dto.FlightOfferDTO;
import com.example.triply.core.search.flight.model.dto.FlightSearchRequestDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class FlightInformationFacadeServiceImplTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private FlightPriceRepository flightPriceRepository;

    @Mock
    private FlightClassRepository flightClassRepository;

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private FlightInformationFacadeServiceImpl flightInformationService;

    private FlightSearchRequestDTO requestDTO;
    private Flight flight;
    private FlightPrice flightPrice;
    private FlightClass flightClass;
    private Airline airline;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        requestDTO = new FlightSearchRequestDTO();
        requestDTO.setOrigin("SIN");
        requestDTO.setDestination("CDG");
        requestDTO.setDepartureDate(LocalDate.of(2025, 5, 10));
        // We'll set maxPrice in tests as needed

        airline = new Airline();
        airline.setId(1L);
        airline.setName("Test Airline");

        flight = new Flight();
        flight.setId(101L);
        flight.setOrigin("SIN");
        flight.setDestination("CDG");
        flight.setDepartureTime(LocalDateTime.of(2025, 5, 10, 0, 0));
        flight.setArrivalTime(LocalDateTime.of(2025, 5, 10, 8, 30));
        flight.setFlightNumber("TA123");
        flight.setAirline(airline);

        flightClass = new FlightClass();
        flightClass.setId(201L);
        flightClass.setClassName("Economy");

        flightPrice = new FlightPrice();
        flightPrice.setId(301L);
        flightPrice.setFlight(flight);
        flightPrice.setFlightClass(flightClass);
        flightPrice.setBasePrice(new BigDecimal("499.99"));
    }

    @Test
    void testGetFlightPrices_withMaxPrice() {
        // Given
        requestDTO.setMaxPrice(new BigDecimal("500.00"));

        List<Flight> flights = new ArrayList<>();
        flights.add(flight);

        List<FlightPrice> flightPrices = new ArrayList<>();
        flightPrices.add(flightPrice);

        when(flightRepository.findByOriginAndDestinationAndDepartureTime(
                "SIN",
                "CDG",
                LocalDateTime.of(2025, 5, 10, 0, 0))
        ).thenReturn(flights);

        when(flightPriceRepository.findAllByFlightIdAndByDepartureDateIn(
                List.of(101L),
                LocalDateTime.of(2025, 5, 10, 0, 0))
        ).thenReturn(flightPrices);

        when(flightClassRepository.findAllByIdIn(
                List.of(flightClass.getId()))
        ).thenReturn(List.of(flightClass));

        when(airlineRepository.findAllByIdIn(
                List.of(airline.getId()))
        ).thenReturn(List.of(airline));

        // When
        List<FlightOfferDTO> result = flightInformationService.getFlightPrices(requestDTO);

        // Then
        assertEquals(1, result.size());
        FlightOfferDTO dto = result.get(0);
        assertEquals("SIN", dto.getOrigin());
        assertEquals("CDG", dto.getDestination());
        assertEquals(new BigDecimal("499.99"), dto.getBasePrice());
        assertEquals("Test Airline", dto.getAirlineName());
        assertEquals("Economy", dto.getFlightClassName());
    }

    @Test
    void testGetFlightPrices_withNoMaxPrice() {
        // Given
        requestDTO.setMaxPrice(null);

        List<Flight> flights = Collections.singletonList(flight);
        List<FlightPrice> flightPrices = Collections.singletonList(flightPrice);

        when(flightRepository.findByOriginAndDestinationAndDepartureTime(
                "SIN",
                "CDG",
                LocalDateTime.of(2025, 5, 10, 0, 0))
        ).thenReturn(flights);

        when(flightPriceRepository.findAllByFlightIdAndByDepartureDateIn(
                List.of(101L),
                LocalDateTime.of(2025, 5, 10, 0, 0))
        ).thenReturn(flightPrices);

        when(flightClassRepository.findAllByIdIn(
                List.of(flightClass.getId()))
        ).thenReturn(List.of(flightClass));

        when(airlineRepository.findAllByIdIn(
                List.of(airline.getId()))
        ).thenReturn(List.of(airline));

        // When
        List<FlightOfferDTO> result = flightInformationService.getFlightPrices(requestDTO);

        // Then
        assertEquals(1, result.size());
        FlightOfferDTO dto = result.get(0);
        assertEquals("SIN", dto.getOrigin());
        assertEquals("CDG", dto.getDestination());
        // With no maxPrice, the flight should still be included
        assertEquals(new BigDecimal("499.99"), dto.getBasePrice());
    }
}