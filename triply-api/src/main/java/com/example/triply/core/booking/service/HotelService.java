package com.example.triply.core.booking.service;


import com.example.triply.core.booking.dto.FlightResponse;
import com.example.triply.core.booking.dto.HotelRequest;
import com.example.triply.core.booking.dto.HotelResponse;
import com.example.triply.core.booking.entity.flight.Flight;
import com.example.triply.core.booking.entity.hotel.Hotel;
import com.example.triply.core.booking.repository.HotelRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class HotelService {
    @Autowired
    private HotelRepository hotelRepository;

    public HotelResponse geHotelById(Long hotelId){
        Optional<Hotel> hotel = hotelRepository.findById(hotelId);
        HotelResponse hotelResponse = new HotelResponse();
        hotelResponse.setAvailability(hotel.get().isAvailability());
        hotelResponse.setId(hotel.get().getId());
        hotelResponse.setBasePrice(hotel.get().getBasePrice());
        hotelResponse.setName(hotel.get().getName());
        hotelResponse.setLocation(hotel.get().getLocation());
        return hotelResponse;

    }
}
