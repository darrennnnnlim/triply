package com.example.triply.core.booking.resource;

import com.example.triply.core.booking.dto.FlightBookingResponse;
import com.example.triply.core.booking.dto.FlightRequest;
import com.example.triply.core.booking.dto.FlightResponse;
import com.example.triply.core.booking.dto.HotelBookingResponse;
import com.example.triply.core.booking.entity.flight.Flight;
import com.example.triply.core.booking.entity.flight.FlightBooking;
import com.example.triply.core.booking.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/${triply.api-version}/flight")
public class FlightResource {

    @Autowired
    private FlightService flightService;

    @GetMapping("/{flightId}")
    public ResponseEntity<FlightResponse> getByFlightId(@PathVariable Long flightId) {
        FlightResponse bookings = flightService.getFlightById(flightId);
        return ResponseEntity.ok(bookings);
    }
}
