package com.example.triply.core.booking.mapper.hotel;

import com.example.triply.core.auth.entity.User;
import com.example.triply.core.auth.repository.UserRepository;
import com.example.triply.core.booking.dto.HotelBookingDTO;
import com.example.triply.core.booking.entity.Booking;
import com.example.triply.core.booking.entity.hotel.HotelBooking;
import com.example.triply.core.booking.repository.BookingRepository;
import com.example.triply.core.hotel.model.entity.Hotel;
import com.example.triply.core.hotel.model.entity.HotelRoomType;
import com.example.triply.core.hotel.repository.HotelRepository;
import com.example.triply.core.hotel.repository.HotelRoomTypeRepository;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HotelBookingMapperTest {

    private HotelRepository hotelRepository;
    private HotelRoomTypeRepository hotelRoomTypeRepository;
    private BookingRepository bookingRepository;
    private UserRepository userRepository;
    private HotelBookingMapper mapper;

    @BeforeEach
    void setUp() {
        hotelRepository = mock(HotelRepository.class);
        hotelRoomTypeRepository = mock(HotelRoomTypeRepository.class);
        bookingRepository = mock(BookingRepository.class);
        userRepository = mock(UserRepository.class);
        mapper = new HotelBookingMapper(hotelRepository, hotelRoomTypeRepository, bookingRepository, userRepository);
    }

    @Test
    void testToDto_validEntity_returnsDto() {
        LocalDateTime checkInDate = LocalDateTime.of(2025, 7, 1, 0, 0, 0);
        LocalDateTime checkOutDate = LocalDateTime.of(2025, 7, 5, 0, 0, 0);

        Hotel hotel = new Hotel();
        hotel.setId(10L);

        HotelRoomType roomType = new HotelRoomType();
        roomType.setId(20L);

        Booking booking = new Booking();
        booking.setId(30L);

        User user = new User();
        user.setId(40L);

        HotelBooking entity = new HotelBooking();
        entity.setId(1L);
        entity.setHotel(hotel);
        entity.setHotelRoomType(roomType);
        entity.setBooking(booking);
        entity.setUser(user);
        entity.setCheckIn(checkInDate);
        entity.setCheckOut(checkOutDate);

        HotelBookingDTO dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getHotelId());
        assertEquals(20L, dto.getHotelRoomTypeId());
        assertEquals(30L, dto.getBookingId());
        assertEquals(40L, dto.getUserId());
        assertEquals(checkInDate, dto.getCheckIn());
        assertEquals(checkOutDate, dto.getCheckOut());
    }

    @Test
    void testToDto_nullEntity_returnsNull() {
        assertNull(mapper.toDto((HotelBooking) null));
    }

    @Test
    void testToEntity_validDto_returnsEntityWithRefs() {
        LocalDateTime checkInDate = LocalDateTime.of(2025, 8, 10, 0, 0, 0);
        LocalDateTime checkOutDate = LocalDateTime.of(2025, 8, 12, 0, 0, 0);

        HotelBookingDTO dto = new HotelBookingDTO();
        dto.setId(2L);
        dto.setHotelId(11L);
        dto.setHotelRoomTypeId(21L);
        dto.setBookingId(31L);
        dto.setUserId(41L);
        dto.setCheckIn(checkInDate);
        dto.setCheckOut(checkOutDate);

        Hotel hotel = new Hotel();
        hotel.setId(11L);

        HotelRoomType roomType = new HotelRoomType();
        roomType.setId(21L);

        Booking booking = new Booking();
        booking.setId(31L);

        User user = new User();
        user.setId(41L);

        when(hotelRepository.findById(11L)).thenReturn(Optional.of(hotel));
        when(hotelRoomTypeRepository.findById(21L)).thenReturn(Optional.of(roomType));
        when(bookingRepository.findById(31L)).thenReturn(Optional.of(booking));
        when(userRepository.findById(41L)).thenReturn(Optional.of(user));

        HotelBooking entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(2L, entity.getId());
        assertEquals(hotel, entity.getHotel());
        assertEquals(roomType, entity.getHotelRoomType());
        assertEquals(booking, entity.getBooking());
        assertEquals(user, entity.getUser());
        assertEquals(checkInDate, entity.getCheckIn());
        assertEquals(checkOutDate, entity.getCheckOut());
    }

    @Test
    void testToEntity_missingReferences_setsNulls() {
        LocalDateTime checkInDate = LocalDateTime.of(2025,9,1,0,0,0);
        LocalDateTime checkOutDate = LocalDateTime.of(2025,9,3,0,0,0);

        HotelBookingDTO dto = new HotelBookingDTO();
        dto.setId(3L);
        dto.setHotelId(999L);
        dto.setHotelRoomTypeId(888L);
        dto.setBookingId(777L);
        dto.setUserId(666L);
        dto.setCheckIn(checkInDate);
        dto.setCheckOut(checkOutDate);

        when(hotelRepository.findById(999L)).thenReturn(Optional.empty());
        when(hotelRoomTypeRepository.findById(888L)).thenReturn(Optional.empty());
        when(bookingRepository.findById(777L)).thenReturn(Optional.empty());
        when(userRepository.findById(666L)).thenReturn(Optional.empty());

        HotelBooking entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(3L, entity.getId());
        assertNull(entity.getHotel());
        assertNull(entity.getHotelRoomType());
        assertNull(entity.getBooking());
        assertNull(entity.getUser());
        assertEquals(checkInDate, entity.getCheckIn());
        assertEquals(checkOutDate, entity.getCheckOut());
    }

    @Test
    void testToEntity_nullDto_returnsNull() {
        assertNull(mapper.toEntity((HotelBookingDTO) null));
    }
}
