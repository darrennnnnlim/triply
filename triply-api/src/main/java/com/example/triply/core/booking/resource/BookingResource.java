package com.example.triply.core.booking.resource;

import com.example.triply.core.booking.dto.FlightBookingResponse;
import com.example.triply.core.booking.dto.HotelBookingResponse;
import com.example.triply.core.booking.service.FlightBookingService;
import com.example.triply.core.booking.service.HotelBookingService;
import com.example.triply.core.ratings.dto.RatingResponse;
import com.example.triply.core.ratings.service.RatingService;
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

    @PostMapping("/test")
    public ResponseEntity<?> postTest() {
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }

    @GetMapping("/test")
    public ResponseEntity<?> getTest() {
        return ResponseEntity.status(HttpStatus.OK).body("Success");
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
}
