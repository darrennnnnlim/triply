package com.example.triply.core.hotel.service;


import com.example.triply.core.booking.dto.HotelResponse;
import com.example.triply.core.hotel.model.entity.Hotel;
import com.example.triply.core.hotel.repository.HotelRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class HotelService {
    @Autowired
    private HotelRepository hotelRepository;

    public HotelResponse geHotelById(Long hotelId) {

        Optional<Hotel> hotel = hotelRepository.findById(hotelId);


        if (hotel.isPresent()) {
            HotelResponse hotelResponse = new HotelResponse();

            hotelResponse.setId(hotel.get().getId());
            hotelResponse.setName(hotel.get().getName());
            hotelResponse.setLocation(hotel.get().getLocation());
            hotelResponse.setDescription(hotel.get().getDescription());

            return hotelResponse;
        } else {
            throw new RuntimeException("Hotel not found for id: " + hotelId);
        }
    }

}
