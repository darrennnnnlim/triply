package com.example.triply.core.search.flight.resource;

// FlightSearchController.java
import com.example.triply.core.pricing.flight.implementation.FlightInformationFacadeServiceImpl;
import com.example.triply.core.pricing.flight.model.dto.FlightOfferDTO;
import com.example.triply.core.search.flight.model.dto.FlightSearchRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/${triply.api-version}/flightsearch")
public class FlightSearchResource {

    private final FlightInformationFacadeServiceImpl flightInformationFacadeService;

    public FlightSearchResource(FlightInformationFacadeServiceImpl flightInformationFacadeService) {
        this.flightInformationFacadeService = flightInformationFacadeService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> testConnection() {
        return ResponseEntity.ok("Test successful");
    }

    @PostMapping
    public ResponseEntity<List<FlightOfferDTO>> searchFlights(@RequestBody FlightSearchRequestDTO flightSearchRequest) {
        return ResponseEntity.ok(flightInformationFacadeService.getFlightPrices(flightSearchRequest));
    }
}