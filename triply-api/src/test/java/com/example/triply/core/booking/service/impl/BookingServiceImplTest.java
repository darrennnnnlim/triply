package com.example.triply.core.booking.service.impl;

import com.example.triply.core.booking.dto.BookingDTO;
import com.example.triply.core.booking.dto.HotelBookingAddonDTO;
import com.example.triply.core.booking.dto.HotelBookingDTO;
import com.example.triply.core.booking.dto.flight.FlightBookingAddonDTO;
import com.example.triply.core.booking.dto.flight.FlightBookingDTO;
import com.example.triply.core.booking.entity.Booking;
import com.example.triply.core.booking.entity.BookingStatusEnum;
import com.example.triply.core.booking.entity.flight.FlightBooking;
import com.example.triply.core.booking.entity.flight.FlightBookingAddon;
import com.example.triply.core.booking.entity.hotel.HotelBooking;
import com.example.triply.core.booking.entity.hotel.HotelBookingAddon;
import com.example.triply.core.booking.mapper.BookingMapper;
import com.example.triply.core.booking.mapper.flight.FlightBookingAddonMapper;
import com.example.triply.core.booking.mapper.flight.FlightBookingMapper;
import com.example.triply.core.booking.mapper.hotel.HotelBookingAddonMapper;
import com.example.triply.core.booking.mapper.hotel.HotelBookingMapper;
import com.example.triply.core.booking.repository.flight.FlightBookingAddonRepository;
import com.example.triply.core.booking.repository.hotel.HotelBookingAddonRepository;
import com.example.triply.core.booking.service.template.FlightBookingService;
import com.example.triply.core.booking.service.template.HotelBookingService;
import com.example.triply.core.flight.mapper.AirlineMapper;
import com.example.triply.core.flight.mapper.FlightAddonMapper;
import com.example.triply.core.flight.mapper.FlightMapper;
import com.example.triply.core.flight.model.dto.AirlineDTO;
import com.example.triply.core.flight.model.dto.FlightAddonDTO;
import com.example.triply.core.flight.model.dto.FlightDTO;
import com.example.triply.core.flight.model.entity.Airline;
import com.example.triply.core.flight.model.entity.Flight;
import com.example.triply.core.flight.model.entity.FlightAddon;
import com.example.triply.core.flight.repository.AirlineRepository;
import com.example.triply.core.flight.repository.FlightAddonRepository;
import com.example.triply.core.flight.repository.FlightRepository;
import com.example.triply.core.hotel.mapper.HotelAddonMapper;
import com.example.triply.core.hotel.mapper.HotelMapper;
import com.example.triply.core.hotel.mapper.HotelRoomTypeBasicMapper;
import com.example.triply.core.hotel.model.dto.HotelAddonDTO;
import com.example.triply.core.hotel.model.dto.HotelDTO;
import com.example.triply.core.hotel.model.dto.HotelRoomTypeBasicDTO;
import com.example.triply.core.hotel.model.entity.Hotel;
import com.example.triply.core.hotel.model.entity.HotelAddon;
import com.example.triply.core.hotel.model.entity.HotelRoomType;
import com.example.triply.core.hotel.repository.HotelAddonRepository;
import com.example.triply.core.hotel.repository.HotelRepository;
import com.example.triply.core.hotel.repository.HotelRoomTypeRepository;
import com.example.triply.core.booking.repository.BookingRepository;
import com.example.triply.core.booking.repository.flight.FlightBookingRepository;
import com.example.triply.core.booking.repository.hotel.HotelBookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    private BookingServiceImpl bookingService;

    private BookingRepository bookingRepository;
    private BookingMapper bookingMapper;
    private FlightBookingRepository flightBookingRepository;
    private HotelBookingRepository hotelBookingRepository;
    private AirlineRepository airlineRepository;
    private FlightRepository flightRepository;
    private HotelRepository hotelRepository;
    private HotelRoomTypeRepository hotelRoomTypeRepository;
    private FlightBookingAddonRepository flightBookingAddonRepository;
    private HotelBookingAddonRepository hotelBookingAddonRepository;
    private FlightAddonRepository flightAddonRepository;
    private HotelAddonRepository hotelAddonRepository;

    private FlightBookingAddonMapper flightBookingAddonMapper;
    private HotelBookingAddonMapper hotelBookingAddonMapper;
    private FlightAddonMapper flightAddonMapper;
    private HotelAddonMapper hotelAddonMapper;

    private FlightBookingService flightBookingService;
    private HotelBookingService hotelBookingService;

    private FlightBookingMapper flightBookingMapper;
    private HotelBookingMapper hotelBookingMapper;
    private FlightMapper flightMapper;
    private AirlineMapper airlineMapper;
    private HotelMapper hotelMapper;
    private HotelRoomTypeBasicMapper hotelRoomTypeBasicMapper;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        bookingMapper = mock(BookingMapper.class);
        flightBookingRepository = mock(FlightBookingRepository.class);
        hotelBookingRepository = mock(HotelBookingRepository.class);
        airlineRepository = mock(AirlineRepository.class);
        flightRepository = mock(FlightRepository.class);
        hotelRepository = mock(HotelRepository.class);
        hotelRoomTypeRepository = mock(HotelRoomTypeRepository.class);

        flightBookingAddonRepository = mock(FlightBookingAddonRepository.class);
        hotelBookingAddonRepository = mock(HotelBookingAddonRepository.class);
        flightAddonRepository = mock(FlightAddonRepository.class);
        hotelAddonRepository = mock(HotelAddonRepository.class);

        flightBookingService = mock(FlightBookingService.class);
        hotelBookingService = mock(HotelBookingService.class);

        flightBookingMapper = mock(FlightBookingMapper.class);
        hotelBookingMapper = mock(HotelBookingMapper.class);
        flightMapper = mock(FlightMapper.class);
        airlineMapper = mock(AirlineMapper.class);
        hotelMapper = mock(HotelMapper.class);
        hotelRoomTypeBasicMapper = mock(HotelRoomTypeBasicMapper.class);
        flightBookingAddonMapper = mock(FlightBookingAddonMapper.class);
        hotelBookingAddonMapper = mock(HotelBookingAddonMapper.class);
        flightAddonMapper = mock(FlightAddonMapper.class);
        hotelAddonMapper = mock(HotelAddonMapper.class);

        bookingService = new BookingServiceImpl(
                flightBookingService, hotelBookingService,
                bookingRepository, bookingMapper,
                flightBookingRepository, flightBookingMapper,
                hotelBookingMapper, hotelBookingRepository,
                flightBookingAddonRepository, hotelBookingAddonRepository,
                flightBookingAddonMapper, hotelBookingAddonMapper,
                airlineRepository, flightRepository,
                flightMapper, airlineMapper,
                hotelMapper, hotelRoomTypeBasicMapper,
                hotelRepository, hotelRoomTypeRepository,
                hotelAddonRepository, flightAddonRepository,
                flightAddonMapper, hotelAddonMapper
        );
    }


    @Test
    void processBooking_success_flightAndHotel() {
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setFlightBooking(new FlightBookingDTO());
        bookingDTO.setHotelBooking(new HotelBookingDTO());

        BookingDTO result = bookingService.processBooking(bookingDTO);

        assertEquals(bookingDTO, result);
        verify(flightBookingService).processBooking(bookingDTO);
        verify(hotelBookingService).processBooking(bookingDTO);
    }

    @Test
    void processBooking_invalidRequest() {
        BookingDTO bookingDTO = new BookingDTO();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> bookingService.processBooking(bookingDTO));
        assertEquals("Process Booking Bad Request", exception.getMessage());
    }

    @Test
    void getBooking_success() {
        Booking booking = new Booking();
        booking.setId(1L);

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setId(1L);

        when(bookingRepository.findByUser_Username("testuser")).thenReturn(Collections.singletonList(booking));
        when(bookingMapper.toDto(Collections.singletonList(booking))).thenReturn(Collections.singletonList(bookingDTO));

        when(flightBookingRepository.findByFlightIdIn(anyList())).thenReturn(Collections.emptyList());
        when(hotelBookingRepository.findByHotelIdIn(anyList())).thenReturn(Collections.emptyList());
        when(airlineRepository.findAll()).thenReturn(Collections.emptyList());
        when(flightRepository.findAll()).thenReturn(Collections.emptyList());
        when(hotelRepository.findAll()).thenReturn(Collections.emptyList());
        when(hotelRoomTypeRepository.findAll()).thenReturn(Collections.emptyList());

        when(flightBookingMapper.toDto((FlightBooking) any())).thenReturn(null);
        when(hotelBookingMapper.toDto((HotelBooking) any())).thenReturn(null);
        when(flightMapper.toDto((Flight) any())).thenReturn(null);
        when(airlineMapper.toDto((Airline) any())).thenReturn(null);
        when(hotelMapper.toDto((Hotel) any())).thenReturn(null);
        when(hotelRoomTypeBasicMapper.toDto((HotelRoomType) any())).thenReturn(null);

        assertDoesNotThrow(() -> bookingService.getBooking("testuser"));
    }

    @Test
    void cancelBooking_success() {
        Booking booking = new Booking();
        booking.setId(1L);

        BookingDTO bookingDTO = new BookingDTO();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(bookingDTO);

        BookingDTO result = bookingService.cancelBooking(1L);

        assertNotNull(result);
        assertEquals(BookingStatusEnum.CANCELLED.name(), booking.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void cancelBooking_bookingNotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        BookingDTO result = bookingService.cancelBooking(99L);

        assertNull(result);
    }

    @Test
    void testGetBooking_populatesFlightAndHotelDetails() {
        String username = "john";

        Booking booking = new Booking();
        booking.setId(1L);

        Airline airline = new Airline();
        airline.setId(200L);

        Hotel hotel = new Hotel();
        hotel.setId(300L);

        Flight flight = new Flight();
        flight.setId(100L);
        flight.setAirline(airline);

        FlightBooking flightBooking = new FlightBooking();
        flightBooking.setId(10L);
        flightBooking.setFlight(flight);
        flightBooking.setBooking(booking);

        HotelRoomType roomType = new HotelRoomType();
        roomType.setId(400L);

        BookingDTO bookingDto = new BookingDTO();
        bookingDto.setId(1L);

        HotelBooking hotelBooking = new HotelBooking();
        hotelBooking.setId(20L);
        hotelBooking.setHotel(hotel);
        hotelBooking.setHotelRoomType(roomType);
        hotelBooking.setBooking(booking);

        FlightBookingDTO flightBookingDto = new FlightBookingDTO();
        flightBookingDto.setId(10L);
        flightBookingDto.setFlightId(100L);
        FlightDTO flightDto = new FlightDTO();
        flightDto.setAirlineId(200L);
        flightBookingDto.setFlight(flightDto);
        AirlineDTO airlineDto = new AirlineDTO();

        HotelBookingDTO hotelBookingDto = new HotelBookingDTO();
        hotelBookingDto.setId(20L);
        hotelBookingDto.setHotelId(300L);
        hotelBookingDto.setHotelRoomTypeId(400L);
        HotelDTO hotelDto = new HotelDTO();
        HotelRoomTypeBasicDTO roomTypeDto = new HotelRoomTypeBasicDTO();

        FlightBookingAddonDTO flightBookingAddonDTO = new FlightBookingAddonDTO();
        HotelBookingAddonDTO hotelBookingAddonDTO = new HotelBookingAddonDTO();

        // Mocks
        when(bookingRepository.findByUser_Username(username)).thenReturn(List.of(booking));
        when(bookingMapper.toDto(List.of(booking))).thenReturn(List.of(bookingDto));
        when(flightBookingRepository.findByBookingIdsIn(List.of(1L))).thenReturn(List.of(flightBooking));
        when(hotelBookingRepository.findByBookingIdsIn(List.of(1L))).thenReturn(List.of(hotelBooking));
        when(flightBookingMapper.toDto(flightBooking)).thenReturn(flightBookingDto);
        when(flightMapper.toDto(flight)).thenReturn(flightDto);
        when(airlineMapper.toDto(airline)).thenReturn(airlineDto);
        when(hotelBookingMapper.toDto(hotelBooking)).thenReturn(hotelBookingDto);
        when(hotelMapper.toDto(hotel)).thenReturn(hotelDto);
        when(hotelRoomTypeBasicMapper.toDto(roomType)).thenReturn(roomTypeDto);
        when(airlineRepository.findAll()).thenReturn(List.of(airline));
        when(flightRepository.findAll()).thenReturn(List.of(flight));
        when(hotelRepository.findAll()).thenReturn(List.of(hotel));
        when(hotelRoomTypeRepository.findAll()).thenReturn(List.of(roomType));

        // Addon mocks (required even if not tested here)
        when(flightBookingAddonRepository.findByFlightBookingIdIn(List.of(10L))).thenReturn(List.of());
        when(hotelBookingAddonRepository.findByHotelBookingIdIn(List.of(20L))).thenReturn(List.of());
        when(flightAddonRepository.findAll()).thenReturn(List.of());
        when(hotelAddonRepository.findAll()).thenReturn(List.of());
        when(flightBookingAddonMapper.toDto(any(FlightBookingAddon.class))).thenReturn(flightBookingAddonDTO);
        when(hotelBookingAddonMapper.toDto(any(HotelBookingAddon.class))).thenReturn(hotelBookingAddonDTO);

        // Execute
        List<BookingDTO> result = bookingService.getBooking(username);

        // Verify
        assertNotNull(result.get(0).getFlightBooking());
        assertEquals(flightDto, result.get(0).getFlightBooking().getFlight());
        assertEquals(airlineDto, result.get(0).getFlightBooking().getFlight().getAirline());

        assertNotNull(result.get(0).getHotelBooking());
        assertEquals(hotelDto, result.get(0).getHotelBooking().getHotel());
        assertEquals(roomTypeDto, result.get(0).getHotelBooking().getHotelRoomType());
    }

    @Test
    void testGetBooking_populatesAddonsCorrectly() {
        Booking booking = new Booking();
        booking.setId(1L);

        FlightBooking flightBooking = new FlightBooking();
        flightBooking.setId(101L);
        flightBooking.setBooking(booking);

        HotelBooking hotelBooking = new HotelBooking();
        hotelBooking.setId(201L);
        hotelBooking.setBooking(booking);

        FlightAddon flightAddonEntity = new FlightAddon();
        flightAddonEntity.setId(301L);
        HotelAddon hotelAddonEntity = new HotelAddon();
        hotelAddonEntity.setId(401L);

        FlightBookingAddon flightAddon = new FlightBookingAddon();
        flightAddon.setFlightBooking(flightBooking);
        flightAddon.setFlightAddon(flightAddonEntity);

        HotelBookingAddon hotelAddon = new HotelBookingAddon();
        hotelAddon.setHotelBooking(hotelBooking);
        hotelAddon.setHotelAddon(hotelAddonEntity);

        FlightBookingAddonDTO flightAddonDto = new FlightBookingAddonDTO();
        flightAddonDto.setFlightAddonId(301L);
        FlightAddonDTO flightAddonDetails = new FlightAddonDTO();
        flightAddonDto.setFlightAddon(flightAddonDetails);

        HotelBookingAddonDTO hotelAddonDto = new HotelBookingAddonDTO();
        hotelAddonDto.setHotelAddonId(401L);
        HotelAddonDTO hotelAddonDetails = new HotelAddonDTO();
        hotelAddonDto.setHotelAddon(hotelAddonDetails);

        BookingDTO bookingDto = new BookingDTO();
        bookingDto.setId(1L);
        bookingDto.setFlightBooking(new FlightBookingDTO() {{ setId(101L); }});
        bookingDto.setHotelBooking(new HotelBookingDTO() {{ setId(201L); }});
        bookingDto.setFlightBookingAddon(List.of(flightAddonDto));
        bookingDto.setHotelBookingAddon(List.of(hotelAddonDto));

        // Mocks
        when(bookingRepository.findByUser_Username("john")).thenReturn(List.of(booking));
        when(bookingMapper.toDto(anyList())).thenReturn(List.of(bookingDto));
        when(flightBookingRepository.findByBookingIdsIn(any())).thenReturn(List.of(flightBooking));
        when(hotelBookingRepository.findByBookingIdsIn(any())).thenReturn(List.of(hotelBooking));
        when(flightBookingAddonRepository.findByFlightBookingIdIn(List.of(101L))).thenReturn(List.of(flightAddon));
        when(hotelBookingAddonRepository.findByHotelBookingIdIn(List.of(201L))).thenReturn(List.of(hotelAddon));
        when(flightAddonRepository.findAll()).thenReturn(List.of(flightAddonEntity));
        when(hotelAddonRepository.findAll()).thenReturn(List.of(hotelAddonEntity));
        when(flightBookingAddonMapper.toDto(any(FlightBookingAddon.class))).thenReturn(flightAddonDto);
        when(hotelBookingAddonMapper.toDto(any(HotelBookingAddon.class))).thenReturn(hotelAddonDto);
        when(flightAddonMapper.toDto(any(FlightAddon.class))).thenReturn(flightAddonDetails);
        when(hotelAddonMapper.toDto(any(HotelAddon.class))).thenReturn(hotelAddonDetails);

        // Execute
        List<BookingDTO> result = bookingService.getBooking("john");

        // Verify
        assertEquals(flightAddonDetails, result.get(0).getFlightBookingAddon().get(0).getFlightAddon());
        assertEquals(hotelAddonDetails, result.get(0).getHotelBookingAddon().get(0).getHotelAddon());
    }

}