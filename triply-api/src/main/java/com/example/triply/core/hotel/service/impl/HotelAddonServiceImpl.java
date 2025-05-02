package com.example.triply.core.hotel.service.impl;

import com.example.triply.core.hotel.mapper.HotelAddonMapper;
import com.example.triply.core.hotel.model.dto.HotelAddonDTO;
import com.example.triply.core.hotel.model.dto.HotelAddonResponse;
import com.example.triply.core.hotel.model.entity.HotelAddon;
import com.example.triply.core.hotel.repository.HotelAddonRepository;
import com.example.triply.core.hotel.service.HotelAddonService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelAddonServiceImpl implements HotelAddonService {

    private final HotelAddonRepository hotelAddonRepository;

    private final HotelAddonMapper hotelAddonMapper;

    public HotelAddonServiceImpl(HotelAddonRepository hotelAddonRepository, HotelAddonMapper hotelAddonMapper) {
        this.hotelAddonRepository = hotelAddonRepository;
        this.hotelAddonMapper = hotelAddonMapper;
    }

    @Override
    public List<HotelAddonDTO> getHotelAddonsByHotelId(Long hotelId) {
        List<HotelAddon> hotelAddonList = hotelAddonRepository.findHotelAddonByHotelId(hotelId);
        return hotelAddonMapper.toDto(hotelAddonList);
    }
}
