package com.example.triply.core.hotel.resource;



import com.example.triply.core.booking.dto.HotelResponse;
import com.example.triply.core.hotel.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/${triply.api-version}/hotel")
public class HotelResource {
    @Autowired
    private HotelService hotelService;

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelResponse> getByHotelId(@PathVariable Long hotelId) {
        HotelResponse bookings = hotelService.geHotelById(hotelId);
        return ResponseEntity.ok(bookings);
    }

}
