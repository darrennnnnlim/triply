package com.example.triply.core.pricing.hotel.resource;

import com.example.triply.core.pricing.hotel.implementation.HotelRoomTypeWriteFacadeServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/${triply.api-version}/hotelpricewrite")
public class HotelRoomWriteFacadeResource {

    private final HotelRoomTypeWriteFacadeServiceImpl hotelRoomPriceWriteFacadeService;


    public HotelRoomWriteFacadeResource(HotelRoomTypeWriteFacadeServiceImpl hotelRoomPriceWriteFacadeService) {
        this.hotelRoomPriceWriteFacadeService = hotelRoomPriceWriteFacadeService;
    }

    @PostMapping("updateExisting")
    ResponseEntity<BigDecimal> updateExistingHotelPrice(@RequestParam("hotelRoomTypeId") Long hotelRoomTypeId,
                                                                @RequestParam("newBasePrice") BigDecimal newBasePrice) {
        hotelRoomPriceWriteFacadeService.updateExistingHotelRoomType(hotelRoomTypeId, newBasePrice);
        return new ResponseEntity<>(newBasePrice, HttpStatus.OK);
    }

}
