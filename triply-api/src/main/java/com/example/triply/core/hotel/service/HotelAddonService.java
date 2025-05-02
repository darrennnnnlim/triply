package com.example.triply.core.hotel.service;

import com.example.triply.core.hotel.model.dto.HotelAddonDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface HotelAddonService {
    public List<HotelAddonDTO> getHotelAddonsByHotelId(Long hotelId);
}
