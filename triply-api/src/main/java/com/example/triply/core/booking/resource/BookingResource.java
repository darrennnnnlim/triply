package com.example.triply.core.booking.resource;

import com.example.triply.core.booking.dto.BookingDTO;
import com.example.triply.core.booking.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/${triply.api-version}/booking")
public class BookingResource {

    private final BookingService bookingService;

    public BookingResource(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/test")
    public ResponseEntity<?> postTest() {
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }

    @GetMapping("/test")
    public ResponseEntity<?> getTest() {
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }

    @PostMapping("/createBooking")
    public ResponseEntity<BookingDTO> createBooking(@RequestBody BookingDTO bookingDTO) {
        BookingDTO booking = bookingService.processBooking(bookingDTO);
        return ResponseEntity.status(HttpStatus.OK).body(booking);
    }
}
