package com.example.triply.core.booking.resource;

import com.example.triply.core.auth.service.JwtService;
import com.example.triply.core.booking.dto.BookingDTO;
import com.example.triply.core.booking.service.BookingService;
import com.example.triply.core.booking.service.template.FlightBookingService;
import com.example.triply.core.booking.service.template.HotelBookingService;
import com.example.triply.common.filter.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BookingResourceTest {

    private MockMvc mockMvc;

    private BookingService bookingService;
    private FlightBookingService flightBookingService;
    private HotelBookingService hotelBookingService;
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        bookingService = mock(BookingService.class);
        flightBookingService = mock(FlightBookingService.class);
        hotelBookingService = mock(HotelBookingService.class);
        jwtAuthenticationFilter = mock(JwtAuthenticationFilter.class);
        jwtService = mock(JwtService.class);

        BookingResource bookingResource = new BookingResource(bookingService, jwtAuthenticationFilter, jwtService);

        // Inject field-based services manually (since they use @Autowired)
        ReflectionTestUtils.setField(bookingResource, "flightBookingService", flightBookingService);
        ReflectionTestUtils.setField(bookingResource, "hotelBookingService", hotelBookingService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(bookingResource)
                .addPlaceholderValue("triply.api-version", "v1")
                .build();
    }

    @Test
    void testGetFlightBookingsByUserId() throws Exception {
        when(flightBookingService.getBookingByUserId(1L))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/booking/flight/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testCreateBooking() throws Exception {
        BookingDTO bookingDTO = new BookingDTO();
        when(bookingService.processBooking(any())).thenReturn(bookingDTO);

        mockMvc.perform(post("/api/v1/booking/createBooking")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void testCancelBooking() throws Exception {
        BookingDTO bookingDTO = new BookingDTO();
        when(bookingService.cancelBooking(anyLong())).thenReturn(bookingDTO);

        mockMvc.perform(put("/api/v1/booking/cancel")
                        .contentType("application/json")
                        .content("1"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetBookings() throws Exception {
        // Mock expected behavior
        String mockToken = "mock-token";
        String mockUsername = "john_doe";

        when(jwtAuthenticationFilter.extractAccessTokenFromCookie(any(HttpServletRequest.class)))
                .thenReturn(mockToken);

        when(jwtService.extractUsername(eq(mockToken), eq(false)))
                .thenReturn(mockUsername);

        when(bookingService.getBooking(eq(mockUsername)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/booking/bookings"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testGetFlightBookingByBookingId() throws Exception {
        when(flightBookingService.getBookingByBookingId(100L, 1L))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/booking/flightBooking/100/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testGetHotelBookingByUserId() throws Exception {
        when(hotelBookingService.getBookingByUserId(1L))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/booking/hotel/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testGetHotelBookingByBookingId() throws Exception {
        when(hotelBookingService.getBookingByBookingId(200L, 1L))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/booking/hotelBooking/200/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

}
