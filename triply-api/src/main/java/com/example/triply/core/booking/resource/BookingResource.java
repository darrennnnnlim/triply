package com.example.triply.core.booking.resource;

import com.example.triply.common.filter.JwtAuthenticationFilter;
import com.example.triply.core.auth.service.JwtService;
import com.example.triply.core.booking.dto.BookingDTO;
import com.example.triply.core.booking.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import com.example.triply.core.booking.dto.FlightBookingResponse;
import com.example.triply.core.booking.dto.HotelBookingResponse;
import com.example.triply.core.booking.service.template.FlightBookingService;
import com.example.triply.core.booking.service.template.HotelBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/${triply.api-version}/booking")
public class BookingResource {
    @Autowired
    private FlightBookingService flightBookingService;

    @Autowired
    private HotelBookingService hotelBookingService;

    private final BookingService bookingService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtService jwtService;

    public BookingResource(BookingService bookingService, JwtAuthenticationFilter jwtAuthenticationFilter, JwtService jwtService) {
        this.bookingService = bookingService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtService = jwtService;
    }

    @GetMapping("/flight/user/{userId}")
    public ResponseEntity<List<FlightBookingResponse>> getFlightBookingByUserId(@PathVariable Long userId) {
        List<FlightBookingResponse> bookings = flightBookingService.getBookingByUserId(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/flightBooking/{bookingId}/{userId}")
    public ResponseEntity<List<FlightBookingResponse>> getFlightBookingByBookingId(@PathVariable Long bookingId, @PathVariable Long userId) {
        List<FlightBookingResponse> bookings = flightBookingService.getBookingByBookingId(bookingId, userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/hotel/user/{userId}")
    public ResponseEntity<List<HotelBookingResponse>> getHotelBookingByUserId(@PathVariable Long userId) {
        List<HotelBookingResponse> bookings = hotelBookingService.getBookingByUserId(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/hotelBooking/{bookingId}/{userId}")
    public ResponseEntity<List<HotelBookingResponse>> getHotelBookingByBookingId(@PathVariable Long bookingId, @PathVariable Long userId) {
        List<HotelBookingResponse> bookings = hotelBookingService.getBookingByBookingId(bookingId, userId);
        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/createBooking")
    public ResponseEntity<BookingDTO> createBooking(@RequestBody BookingDTO bookingDTO) {
        BookingDTO booking = bookingService.processBooking(bookingDTO);
        return ResponseEntity.status(HttpStatus.OK).body(booking);
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingDTO>> getBookings(HttpServletRequest request) {
        String accessToken = jwtAuthenticationFilter.extractAccessTokenFromCookie(request);
        String username = jwtService.extractUsername(accessToken, false);
        List<BookingDTO> bookingDTOList = bookingService.getBooking(username);
        return ResponseEntity.status(HttpStatus.OK).body(bookingDTOList);
    }

    @PutMapping("/cancel")
    public ResponseEntity<BookingDTO> cancelBooking(@RequestBody Long bookingId) {
        BookingDTO booking = bookingService.cancelBooking(bookingId);
        return ResponseEntity.status(HttpStatus.OK).body(booking);
    }
}
