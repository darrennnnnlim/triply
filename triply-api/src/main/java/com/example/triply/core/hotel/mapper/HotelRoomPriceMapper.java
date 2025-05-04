package com.example.triply.core.hotel.mapper;

import com.example.triply.common.mapper.BaseMapper;
import com.example.triply.core.hotel.model.dto.HotelRoomPriceDTO;
import com.example.triply.core.hotel.model.entity.HotelRoomPrice;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HotelRoomPriceMapper implements BaseMapper<HotelRoomPrice, HotelRoomPriceDTO> {

    @Override
    public HotelRoomPriceDTO toDto(HotelRoomPrice entity) {
        HotelRoomPriceDTO hotelPriceDTO = new HotelRoomPriceDTO();
        hotelPriceDTO.setHotelRoomTypeId(entity.getHotelRoomType().getId());
        hotelPriceDTO.setId(entity.getId());
        hotelPriceDTO.setStartDate(entity.getStartDate());
        hotelPriceDTO.setEndDate(entity.getEndDate());
        hotelPriceDTO.setPrice(entity.getPrice());
        return hotelPriceDTO;
    }

    @Override
    public HotelRoomPrice toEntity(HotelRoomPriceDTO dto) { return null; }

    @Override
    public List<HotelRoomPriceDTO> toDto(List<HotelRoomPrice> entities) {
        return List.of();
    }

    @Override
    public List<HotelRoomPrice> toEntity(List<HotelRoomPriceDTO> dto) {
        return List.of();
    }
}
