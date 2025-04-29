package com.example.triply.core.booking.service.impl;

import com.example.triply.core.booking.dto.BookingDTO;
import com.example.triply.core.booking.dto.HotelBookingDTO;
import com.example.triply.core.booking.dto.flight.FlightBookingDTO;
import com.example.triply.core.booking.entity.Booking;
import com.example.triply.core.booking.entity.BookingStatusEnum;
import com.example.triply.core.booking.entity.flight.FlightBooking;
import com.example.triply.core.booking.entity.hotel.HotelBooking;
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
import com.example.triply.core.flight.model.entity.Airline;
import com.example.triply.core.flight.model.entity.Flight;
import com.example.triply.core.flight.repository.AirlineRepository;
import com.example.triply.core.flight.repository.FlightAddonRepository;
import com.example.triply.core.flight.repository.FlightRepository;
import com.example.triply.core.hotel.mapper.HotelAddonMapper;
import com.example.triply.core.hotel.mapper.HotelMapper;
import com.example.triply.core.hotel.mapper.HotelRoomTypeBasicMapper;
import com.example.triply.core.hotel.model.entity.Hotel;
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
}