package com.example.triply.core.search.flight.resource;

// FlightSearchController.java
import com.example.triply.core.pricing.implementation.FlightInformationFacadeServiceImpl;
import com.example.triply.core.pricing.model.dto.FlightOfferDTO;
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
        // Dummy response to simulate search logic
        String responseMessage = "Searching flights from " + flightSearchRequest.getOrigin()
                                 + " to " + flightSearchRequest.getDestination()
                                 + " between " + flightSearchRequest.getDepartureDate()
                                 + " and " + flightSearchRequest.getArrivalDate()
                                 + " under price " + flightSearchRequest.getMaxPrice();
        return ResponseEntity.ok(flightInformationFacadeService.getFlightPrices(flightSearchRequest));
//        return ResponseEntity.ok(responseMessage);
    }
}