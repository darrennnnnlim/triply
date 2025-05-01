package com.example.triply.core.booking.service.template;

import com.example.triply.core.booking.dto.BookingDTO;
import com.example.triply.core.booking.dto.FlightBookingResponse;
import com.example.triply.core.booking.dto.flight.FlightBookingDTO;
import com.example.triply.core.booking.entity.Booking;
import com.example.triply.core.booking.entity.flight.FlightBooking;
import com.example.triply.core.booking.mapper.BookingMapper;
import com.example.triply.core.booking.mapper.flight.FlightBookingAddonMapper;
import com.example.triply.core.booking.mapper.flight.FlightBookingMapper;
import com.example.triply.core.booking.repository.BookingRepository;
import com.example.triply.core.booking.repository.flight.FlightBookingAddonRepository;
import com.example.triply.core.booking.repository.flight.FlightBookingRepository;
import com.example.triply.core.flight.model.entity.Flight;
import com.example.triply.core.flight.model.entity.FlightAddon;
import com.example.triply.core.flight.model.entity.FlightAddonPrice;
import com.example.triply.core.flight.model.entity.FlightClass;
import com.example.triply.core.flight.model.entity.FlightPrice;
import com.example.triply.core.flight.repository.FlightAddonPriceRepository;
import com.example.triply.core.flight.repository.FlightClassRepository;
import com.example.triply.core.flight.repository.FlightPriceRepository;
import com.example.triply.core.flight.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FlightBookingServiceTest {

    private FlightBookingService flightBookingService;

    private FlightRepository flightRepository;
    private FlightClassRepository flightClassRepository;
    private FlightAddonPriceRepository flightAddonPriceRepository;
    private FlightPriceRepository flightPriceRepository;
    private BookingMapper bookingMapper;
    private FlightBookingMapper flightBookingMapper;
    private FlightBookingRepository flightBookingRepository;
    private FlightBookingAddonRepository flightBookingAddonRepository;
    private FlightBookingAddonMapper flightBookingAddonMapper;
    private BookingRepository bookingRepository;
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        flightRepository = mock(FlightRepository.class);
        flightClassRepository = mock(FlightClassRepository.class);
        flightAddonPriceRepository = mock(FlightAddonPriceRepository.class);
        flightPriceRepository = mock(FlightPriceRepository.class);
        bookingMapper = mock(BookingMapper.class);
        flightBookingMapper = mock(FlightBookingMapper.class);
        flightBookingRepository = mock(FlightBookingRepository.class);
        flightBookingAddonRepository = mock(FlightBookingAddonRepository.class);
        flightBookingAddonMapper = mock(FlightBookingAddonMapper.class);
        bookingRepository = mock(BookingRepository.class);
        eventPublisher = mock(ApplicationEventPublisher.class);

        flightBookingService = new FlightBookingService(
                flightRepository, flightClassRepository,
                flightAddonPriceRepository, flightPriceRepository,
                bookingMapper, flightBookingMapper,
                flightBookingRepository, flightBookingAddonRepository,
                flightBookingAddonMapper, bookingRepository,
                eventPublisher);
    }

    @Test
    void validateBooking_validRequest_doesNotThrow() {
        BookingDTO bookingDTO = new BookingDTO();
        FlightBookingDTO flightBookingDTO = new FlightBookingDTO();
        flightBookingDTO.setFlightId(1L);
        flightBookingDTO.setFlightClassId(2L);
        flightBookingDTO.setDepartureDate(LocalDateTime.now().plusDays(1));
        bookingDTO.setFlightBooking(flightBookingDTO);

        when(flightRepository.findById(1L)).thenReturn(Optional.of(new Flight()));
        when(flightClassRepository.findById(2L)).thenReturn(Optional.of(new FlightClass()));

        assertDoesNotThrow(() -> flightBookingService.validateBooking(bookingDTO));
    }

    @Test
    void calculateAddonPrice_withAddons_returnsCorrectTotal() {
        BookingDTO bookingDTO = new BookingDTO();
        FlightBookingDTO flightBookingDTO = new FlightBookingDTO();
        flightBookingDTO.setFlightId(1L);
        bookingDTO.setFlightBooking(flightBookingDTO);

        com.example.triply.core.booking.dto.flight.FlightBookingAddonDTO addonDTO = new com.example.triply.core.booking.dto.flight.FlightBookingAddonDTO();
        addonDTO.setFlightAddonId(100L);
        addonDTO.setQuantity(2);
        bookingDTO.setFlightBookingAddon(List.of(addonDTO));

        FlightAddon addonEntity = new FlightAddon();
        addonEntity.setId(100L);

        FlightAddonPrice addonPrice = new FlightAddonPrice();
        addonPrice.setFlightAddon(addonEntity);
        addonPrice.setPrice(BigDecimal.TEN);

        when(flightAddonPriceRepository.findByFlightAndFlightAddonIn(eq(1L), anyList()))
                .thenReturn(List.of(addonPrice));

        BigDecimal result = flightBookingService.calculateAddonPrice(bookingDTO);

        assertEquals(BigDecimal.valueOf(20), result);
    }

    @Test
    void calculateTotalPrice_appliesAllCalculations() {
        BookingDTO bookingDTO = new BookingDTO();
        FlightBookingDTO flightBookingDTO = new FlightBookingDTO();
        flightBookingDTO.setFlightId(1L);
        flightBookingDTO.setFlightClassId(2L);
        bookingDTO.setFlightBooking(flightBookingDTO);

        FlightPrice price = new FlightPrice();
        price.setBasePrice(BigDecimal.valueOf(100));
        price.setDiscount(BigDecimal.valueOf(10));
        price.setSurgeMultiplier(BigDecimal.valueOf(1.2));

        when(flightPriceRepository.findByFlightAndFlightClass(1L, 2L)).thenReturn(Optional.of(price));

        flightBookingService.calculateTotalPrice(bookingDTO, BigDecimal.valueOf(10));

        assertNotNull(bookingDTO.getFinalPrice());
        assertEquals(0, bookingDTO.getFinalPrice().compareTo(new BigDecimal("118.00")));
    }

    @Test
    void createBooking_createsAndSavesAll() {
        BookingDTO request = new BookingDTO();
        request.setFlightBooking(new FlightBookingDTO());

        Booking savedBooking = new Booking();
        savedBooking.setId(1L);
        savedBooking.setStatus("PENDING");
        savedBooking.setBookingTime(LocalDateTime.now());

        FlightBooking savedFlightBooking = new FlightBooking();

        when(bookingMapper.toEntity(request)).thenReturn(new Booking());
        when(bookingRepository.save(any())).thenReturn(savedBooking);
        when(flightBookingMapper.toEntity((FlightBookingDTO) any())).thenReturn(new FlightBooking());
        when(flightBookingRepository.save(any())).thenReturn(savedFlightBooking);
        when(flightBookingMapper.toDto(savedFlightBooking)).thenReturn(new FlightBookingDTO());

        Booking result = flightBookingService.createBooking(request);

        assertEquals(savedBooking.getId(), result.getId());
        assertEquals("PENDING", result.getStatus());
    }

    @Test
    void getBookingByUserId_returnsResponses() {
        FlightBooking booking = mock(FlightBooking.class);
        Flight flight = new Flight(); flight.setId(99L);
        Booking parentBooking = new Booking(); parentBooking.setId(88L);

        when(booking.getFlight()).thenReturn(flight);
        when(booking.getBooking()).thenReturn(parentBooking);
        when(booking.getDepartureDate()).thenReturn(LocalDateTime.of(2025, 1, 1, 10, 0));

        when(flightBookingRepository.findByUserId(1L)).thenReturn(List.of(booking));

        List<FlightBookingResponse> result = flightBookingService.getBookingByUserId(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getUserId());
        assertEquals(88L, result.getFirst().getBookingId());
        assertEquals("2025-01-01 10:00:00", result.getFirst().getDepartureDate());
        assertEquals(99L, result.getFirst().getFlightId());
    }

    @Test
    void getBookingByBookingId_returnsResponses() {
        FlightBooking booking = mock(FlightBooking.class);
        Flight flight = new Flight(); flight.setId(77L);
        Booking parentBooking = new Booking(); parentBooking.setId(66L);

        when(booking.getFlight()).thenReturn(flight);
        when(booking.getBooking()).thenReturn(parentBooking);
        when(booking.getDepartureDate()).thenReturn(LocalDateTime.of(2025, 2, 2, 12, 30));

        when(flightBookingRepository.findByBookingId(66L)).thenReturn(List.of(booking));

        List<FlightBookingResponse> result = flightBookingService.getBookingByBookingId(66L, 9L);

        assertEquals(1, result.size());
        assertEquals(9L, result.getFirst().getUserId());
        assertEquals("2025-02-02 12:30:00", result.getFirst().getDepartureDate());
        assertEquals(77L, result.getFirst().getFlightId());
    }

    @Test
    void validateBooking_missingFlightBooking_throwsException() {
        BookingDTO bookingDTO = new BookingDTO(); // no flightBooking
        Exception ex = assertThrows(IllegalArgumentException.class, () -> flightBookingService.validateBooking(bookingDTO));
        assertEquals("Invalid request: Flight Booking details are required", ex.getMessage());
    }

    @Test
    void validateBooking_missingIds_throwsException() {
        FlightBookingDTO flightBookingDTO = new FlightBookingDTO(); // missing IDs
        flightBookingDTO.setDepartureDate(LocalDateTime.now().plusDays(1));
        BookingDTO dto = new BookingDTO();
        dto.setFlightBooking(flightBookingDTO);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> flightBookingService.validateBooking(dto));
        assertEquals("Flight ID and Flight Class ID are required", ex.getMessage());
    }

    @Test
    void validateBooking_pastDeparture_throwsException() {
        FlightBookingDTO flightBookingDTO = new FlightBookingDTO();
        flightBookingDTO.setFlightId(1L);
        flightBookingDTO.setFlightClassId(2L);
        flightBookingDTO.setDepartureDate(LocalDateTime.now().minusDays(1));
        BookingDTO dto = new BookingDTO();
        dto.setFlightBooking(flightBookingDTO);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> flightBookingService.validateBooking(dto));
        assertEquals("Departure Date cannot be in the past", ex.getMessage());
    }

    @Test
    void validateBooking_invalidFlightId_throwsException() {
        FlightBookingDTO flightBookingDTO = new FlightBookingDTO();
        flightBookingDTO.setFlightId(999L);
        flightBookingDTO.setFlightClassId(2L);
        flightBookingDTO.setDepartureDate(LocalDateTime.now().plusDays(1));
        BookingDTO dto = new BookingDTO();
        dto.setFlightBooking(flightBookingDTO);

        when(flightRepository.findById(999L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> flightBookingService.validateBooking(dto));
        assertEquals("Flight ID is not a valid existing flight data", ex.getMessage());
    }

    @Test
    void validateBooking_invalidFlightClassId_throwsException() {
        FlightBookingDTO flightBookingDTO = new FlightBookingDTO();
        flightBookingDTO.setFlightId(1L);
        flightBookingDTO.setFlightClassId(999L);
        flightBookingDTO.setDepartureDate(LocalDateTime.now().plusDays(1));
        BookingDTO dto = new BookingDTO();
        dto.setFlightBooking(flightBookingDTO);

        when(flightRepository.findById(1L)).thenReturn(Optional.of(new Flight()));
        when(flightClassRepository.findById(999L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> flightBookingService.validateBooking(dto));
        assertEquals("Flight Class ID is not a valid existing flight class data", ex.getMessage());
    }

    @Test
    void calculateAddonPrice_noAddons_returnsZero() {
        BookingDTO dto = new BookingDTO();
        dto.setFlightBooking(new FlightBookingDTO());
        dto.setFlightBookingAddon(null); // or empty list

        BigDecimal result = flightBookingService.calculateAddonPrice(dto);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void calculateTotalPrice_priceNotFound_throwsException() {
        BookingDTO bookingDTO = new BookingDTO();
        FlightBookingDTO flightBookingDTO = new FlightBookingDTO();
        flightBookingDTO.setFlightId(1L);
        flightBookingDTO.setFlightClassId(2L);
        bookingDTO.setFlightBooking(flightBookingDTO);

        when(flightPriceRepository.findByFlightAndFlightClass(1L, 2L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> flightBookingService.calculateTotalPrice(bookingDTO, BigDecimal.TEN));
        assertEquals("Flight ID and Flight Class ID combination not found in Flight Price", ex.getMessage());
    }
}