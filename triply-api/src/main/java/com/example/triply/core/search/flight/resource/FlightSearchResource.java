package com.example.triply.core.search.flight.resource;

// FlightSearchController.java
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.example.triply.core.search.flight.*;

@RestController
@RequestMapping("/api/v1/flightsearch")
public class FlightSearchResource {

    @GetMapping("/test")
    public ResponseEntity<String> testConnection() {
        return ResponseEntity.ok("Test successful");
    }

    @PostMapping
    public ResponseEntity<String> searchFlights(@RequestBody FlightSearchDTO searchRequest) {
        // Dummy response to simulate search logic
        // In real use, replace with actual service call and logic
        String responseMessage = "Searching flights from " + searchRequest.getOrigin() 
                                 + " to " + searchRequest.getDestination()
                                 + " between " + searchRequest.getDepartureDate()
                                 + " and " + searchRequest.getArrivalDate()
                                 + " under price " + searchRequest.getMaxPrice();
        return ResponseEntity.ok(responseMessage);   
    }
}