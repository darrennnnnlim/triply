package com.example.triply.core.flight.resource;

import com.example.triply.core.booking.dto.FlightResponse;
import com.example.triply.core.flight.model.dto.FlightAddonResponse;
import com.example.triply.core.flight.model.entity.FlightAddon;
import com.example.triply.core.flight.service.FlightAddonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/${triply.api-version}/flightaddon")
public class FlightAddonResource {

    private final FlightAddonService flightAddonService;

    public FlightAddonResource(FlightAddonService flightAddonService) {
        this.flightAddonService = flightAddonService;
    }

    @GetMapping("/{flightId}")
    public ResponseEntity<List<FlightAddonResponse>> getByFlightId(@PathVariable Long flightId) {
        List<FlightAddonResponse> result = flightAddonService.getFlightAddonsByFlightId(flightId);
        return ResponseEntity.ok(result);
    }
}
