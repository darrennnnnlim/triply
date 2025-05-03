package com.example.triply.core.booking.mapper.flight;

import com.example.triply.core.auth.entity.User;
import com.example.triply.core.auth.repository.UserRepository;
import com.example.triply.core.booking.dto.flight.FlightBookingDTO;
import com.example.triply.core.booking.entity.Booking;
import com.example.triply.core.booking.entity.flight.FlightBooking;
import com.example.triply.core.booking.repository.BookingRepository;
import com.example.triply.core.flight.mapper.FlightMapper;
import com.example.triply.core.flight.model.dto.FlightDTO;
import com.example.triply.core.flight.model.entity.Flight;
import com.example.triply.core.flight.model.entity.FlightClass;
import com.example.triply.core.flight.repository.FlightClassRepository;
import com.example.triply.core.flight.repository.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FlightBookingMapperTest {

    private FlightRepository flightRepository;
    private FlightClassRepository flightClassRepository;
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private FlightMapper flightMapper;
    private FlightBookingMapper flightBookingMapper;

    @BeforeEach
    void setUp() {
        flightRepository = mock(FlightRepository.class);
        flightClassRepository = mock(FlightClassRepository.class);
        bookingRepository = mock(BookingRepository.class);
        userRepository = mock(UserRepository.class);
        flightMapper = mock(FlightMapper.class);

        flightBookingMapper = new FlightBookingMapper(
                flightRepository,
                flightClassRepository,
                bookingRepository,
                userRepository,
                flightMapper
        );
    }

    @Test
    void testToDto_validEntity_returnsDto() {
        LocalDateTime departureDate = LocalDateTime.of(2025, 5, 1, 0, 0, 0);

        Flight flight = new Flight();
        flight.setId(101L);

        FlightClass flightClass = new FlightClass();
        flightClass.setId(201L);

        Booking booking = new Booking();
        booking.setId(301L);

        User user = new User();
        user.setId(401L);

        FlightBooking entity = new FlightBooking();
        entity.setId(1L);
        entity.setFlight(flight);
        entity.setFlightClass(flightClass);
        entity.setBooking(booking);
        entity.setUser(user);
        entity.setDepartureDate(departureDate);

        FlightDTO mockFlightDTO = new FlightDTO();
        mockFlightDTO.setId(101L);
        when(flightRepository.findById(101L)).thenReturn(Optional.of(flight));
        when(flightMapper.toDto(flight)).thenReturn(mockFlightDTO);

        FlightBookingDTO dto = flightBookingMapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(101L, dto.getFlightId());
        assertEquals(201L, dto.getFlightClassId());
        assertEquals(301L, dto.getBookingId());
        assertEquals(401L, dto.getUserId());
        assertEquals(departureDate, dto.getDepartureDate());
        assertEquals(mockFlightDTO, dto.getFlight());
    }

    @Test
    void testToDto_nullEntity_returnsNull() {
        assertNull(flightBookingMapper.toDto((FlightBooking) null));
    }

    @Test
    void testToEntity_validDto_returnsEntity() {
        LocalDateTime departureDate = LocalDateTime.of(2025, 6, 15, 0, 0, 0);

        FlightBookingDTO dto = new FlightBookingDTO();
        dto.setId(10L);
        dto.setFlightId(101L);
        dto.setFlightClassId(201L);
        dto.setBookingId(301L);
        dto.setUserId(401L);
        dto.setDepartureDate(departureDate);

        Flight flight = new Flight();
        flight.setId(101L);
        FlightClass flightClass = new FlightClass();
        flightClass.setId(201L);
        Booking booking = new Booking();
        booking.setId(301L);
        User user = new User();
        user.setId(401L);

        when(flightRepository.findById(101L)).thenReturn(Optional.of(flight));
        when(flightClassRepository.findById(201L)).thenReturn(Optional.of(flightClass));
        when(bookingRepository.findById(301L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(401L)).thenReturn(Optional.of(user));

        FlightBooking entity = flightBookingMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(10L, entity.getId());
        assertEquals(flight, entity.getFlight());
        assertEquals(flightClass, entity.getFlightClass());
        assertEquals(booking, entity.getBooking());
        assertEquals(user, entity.getUser());
        assertEquals(departureDate, entity.getDepartureDate());
    }

    @Test
    void testToEntity_missingReferences_setsNulls() {
        LocalDateTime dtoDepartureDate = LocalDateTime.of(2025,8,8,0,0,0);

        FlightBookingDTO dto = new FlightBookingDTO();
        dto.setId(99L);
        dto.setFlightId(111L);
        dto.setFlightClassId(222L);
        dto.setBookingId(333L);
        dto.setUserId(444L);
        dto.setDepartureDate(dtoDepartureDate);

        when(flightRepository.findById(111L)).thenReturn(Optional.empty());
        when(flightClassRepository.findById(222L)).thenReturn(Optional.empty());
        when(bookingRepository.findById(333L)).thenReturn(Optional.empty());
        when(userRepository.findById(444L)).thenReturn(Optional.empty());

        FlightBooking entity = flightBookingMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(99L, entity.getId());
        assertNull(entity.getFlight());
        assertNull(entity.getFlightClass()); // Important fix
        assertNull(entity.getBooking());
        assertNull(entity.getUser());
        assertEquals(dtoDepartureDate, entity.getDepartureDate());
    }

    @Test
    void testToEntity_nullDto_returnsNull() {
        assertNull(flightBookingMapper.toEntity((FlightBookingDTO) null));
    }
}
