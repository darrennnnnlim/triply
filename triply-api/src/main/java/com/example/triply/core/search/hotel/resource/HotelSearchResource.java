package com.example.triply.core.search.hotel.resource;

import com.example.triply.core.pricing.hotel.implementation.HotelInformationFacadeServiceImpl;
import com.example.triply.core.pricing.hotel.model.dto.HotelOfferDTO;
import com.example.triply.core.search.hotel.model.dto.HotelSearchRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/${triply.api-version}/hotelsearch")
public class HotelSearchResource {

    private final HotelInformationFacadeServiceImpl hotelInformationFacadeService;

    public HotelSearchResource(HotelInformationFacadeServiceImpl hotelInformationFacadeService) {
        this.hotelInformationFacadeService = hotelInformationFacadeService;
    }

    @PostMapping
    public ResponseEntity<List<HotelOfferDTO>> searchHotels(@RequestBody HotelSearchRequestDTO hotelSearchRequest) {
        return ResponseEntity.ok(hotelInformationFacadeService.getHotelPrices(hotelSearchRequest));
    }
}