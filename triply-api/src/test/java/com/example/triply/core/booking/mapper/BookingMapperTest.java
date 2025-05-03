package com.example.triply.core.booking.mapper;

import com.example.triply.core.auth.entity.User;
import com.example.triply.core.auth.repository.UserRepository;
import com.example.triply.core.booking.dto.BookingDTO;
import com.example.triply.core.booking.entity.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingMapperTest {

    private UserRepository userRepository;
    private BookingMapper bookingMapper;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        bookingMapper = new BookingMapper(userRepository);
    }

    @Test
    void testToDto_validEntity_returnsDto() {
        User user = new User();
        user.setId(42L);

        LocalDateTime bookingTime = LocalDateTime.of(2025, 5, 1, 10, 30);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setUser(user);
        booking.setFinalPrice(BigDecimal.valueOf(150.75));
        booking.setStatus("CONFIRMED");
        booking.setBookingTime(bookingTime);

        BookingDTO dto = bookingMapper.toDto(booking);

        assertNotNull(dto);
        assertEquals(Long.valueOf(1L), dto.getId());
        assertEquals(Long.valueOf(42L), dto.getUserId());
        assertEquals(BigDecimal.valueOf(150.75), dto.getFinalPrice());
        assertEquals("CONFIRMED", dto.getStatus());
        assertEquals(bookingTime, dto.getBookingTime());
    }

    @Test
    void testToDto_nullEntity_returnsNull() {
        assertNull(bookingMapper.toDto((Booking) null));
    }

    @Test
    void testToEntity_validDto_returnsEntityWithUser() {
        LocalDateTime dtoBookingTime = LocalDateTime.of(2025, 6, 10, 14, 0);

        BookingDTO dto = new BookingDTO();
        dto.setId(5L);
        dto.setUserId(99L);
        dto.setFinalPrice(BigDecimal.valueOf(250.00));
        dto.setStatus("CANCELLED");
        dto.setBookingTime(dtoBookingTime);

        User user = new User();
        user.setId(99L);

        when(userRepository.findById(99L)).thenReturn(Optional.of(user));

        Booking entity = bookingMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(Long.valueOf(5L), entity.getId());
        assertEquals(user, entity.getUser());
        assertEquals(BigDecimal.valueOf(250.00), entity.getFinalPrice());
        assertEquals("CANCELLED", entity.getStatus());
        assertEquals(dtoBookingTime, entity.getBookingTime());
    }

    @Test
    void testToEntity_userNotFound_setsUserToNull() {
        LocalDateTime dtoBookingTime = LocalDateTime.of(2025, 6, 10, 14, 0);

        BookingDTO dto = new BookingDTO();
        dto.setId(10L);
        dto.setUserId(404L);
        dto.setFinalPrice(BigDecimal.TEN);
        dto.setStatus("PENDING");
        dto.setBookingTime(dtoBookingTime);

        when(userRepository.findById(404L)).thenReturn(Optional.empty());

        Booking entity = bookingMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(Long.valueOf(10L), entity.getId());
        assertNull(entity.getUser());
        assertEquals(BigDecimal.TEN, entity.getFinalPrice());
        assertEquals("PENDING", entity.getStatus());
        assertEquals(dtoBookingTime, entity.getBookingTime());
    }

    @Test
    void testToEntity_nullDto_returnsNull() {
        assertNull(bookingMapper.toEntity((BookingDTO) null));
    }
}
