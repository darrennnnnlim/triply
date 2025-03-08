package com.example.triply.core.booking.resource;

import com.example.triply.common.filter.JwtAuthenticationFilter;
import com.example.triply.core.auth.service.JwtService;
import com.example.triply.core.booking.dto.BookingDTO;
import com.example.triply.core.booking.service.BookingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/${triply.api-version}/booking")
public class BookingResource {

    private final BookingService bookingService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtService jwtService;

    public BookingResource(BookingService bookingService, JwtAuthenticationFilter jwtAuthenticationFilter, JwtService jwtService) {
        this.bookingService = bookingService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtService = jwtService;
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
}
