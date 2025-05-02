package com.example.triply.core.hotel.resource;

import com.example.triply.core.hotel.model.dto.HotelAddonDTO;
import com.example.triply.core.hotel.service.HotelAddonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/${triply.api-version}/hoteladdon")
public class HotelAddonResource {

    private final HotelAddonService hotelAddonService;

    public HotelAddonResource(HotelAddonService hotelAddonService) {
        this.hotelAddonService = hotelAddonService;
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<List<HotelAddonDTO>> getByHotelId(@PathVariable Long hotelId) {
        List<HotelAddonDTO> result = hotelAddonService.getHotelAddonsByHotelId(hotelId);
        return ResponseEntity.ok(result);
    }
}
