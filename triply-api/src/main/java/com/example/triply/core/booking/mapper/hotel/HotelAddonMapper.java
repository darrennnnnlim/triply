package com.example.triply.core.booking.mapper.hotel;

import com.example.triply.common.mapper.BaseMapper;
import com.example.triply.core.booking.dto.hotel.HotelAddonDTO;
import com.example.triply.core.booking.entity.hotel.Hotel;
import com.example.triply.core.booking.entity.hotel.HotelAddon;
import com.example.triply.core.booking.repository.hotel.HotelRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class HotelAddonMapper implements BaseMapper<HotelAddon, HotelAddonDTO> {

    private final HotelRepository hotelRepository;

    public HotelAddonMapper(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Override
    public HotelAddonDTO toDto(HotelAddon entity) {
        if (entity == null) {
            return null;
        }

        HotelAddonDTO dto = new HotelAddonDTO();
        dto.setId(entity.getId());
        dto.setHotelId(entity.getHotel().getId());
        dto.setName(entity.getName());
        dto.setPrice(entity.getPrice());

        mapAuditFieldsToDto(entity, dto);

        return dto;
    }

    @Override
    public HotelAddon toEntity(HotelAddonDTO dto) {
        if (dto == null) {
            return null;
        }

        HotelAddon entity = new HotelAddon();
        entity.setId(dto.getId());

        Optional<Hotel> hotelOptional = hotelRepository.findById(dto.getHotelId());
        if (hotelOptional.isPresent()) {
            entity.setHotel(hotelOptional.get());
        } else {
            entity.setHotel(null);
        }

        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());

        mapAuditFieldsToEntity(dto, entity);

        return entity;
    }
}
