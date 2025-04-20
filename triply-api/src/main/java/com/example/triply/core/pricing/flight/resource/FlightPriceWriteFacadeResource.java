package com.example.triply.core.pricing.flight.resource;

import com.example.triply.core.pricing.flight.implementation.FlightPriceWriteFacadeServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/${triply.api-version}/flightpricewrite")
public class FlightPriceWriteFacadeResource {

    private final FlightPriceWriteFacadeServiceImpl flightPriceWriteFacadeService;


    public FlightPriceWriteFacadeResource(FlightPriceWriteFacadeServiceImpl flightPriceWriteFacadeService) {
        this.flightPriceWriteFacadeService = flightPriceWriteFacadeService;
    }

    @PostMapping("updateExisting")
    ResponseEntity<BigDecimal> updateExistingFlightPrice(@RequestParam("flightId") Long flightId,
                                                                @RequestParam("flightClassId") Long flightClassId,
                                                                @RequestParam("newBasePrice") BigDecimal newBasePrice) {
        flightPriceWriteFacadeService.updateExistingFlightPrice(flightId, flightClassId, newBasePrice);
        return new ResponseEntity<>(newBasePrice, HttpStatus.OK);
    }

}
