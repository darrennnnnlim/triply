package com.example.triply.core.hotel.mapper;

import com.example.triply.common.mapper.BaseMapper;
import com.example.triply.core.hotel.model.dto.HotelRoomTypeDTO;
import com.example.triply.core.hotel.model.entity.Hotel;
import com.example.triply.core.hotel.model.entity.HotelRoomType;
import com.example.triply.core.hotel.repository.HotelRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class HotelRoomTypeMapper implements BaseMapper<HotelRoomType, HotelRoomTypeDTO> {
    private final HotelRepository hotelRepository;

    public HotelRoomTypeMapper(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Override
    public HotelRoomTypeDTO toDto(HotelRoomType entity) {
        if (entity == null) {
            return null;
        }

        HotelRoomTypeDTO dto = new HotelRoomTypeDTO();
        dto.setId(entity.getId());
        dto.setHotelId(entity.getHotel().getId());
        dto.setName(entity.getName());
        dto.setBasePrice(entity.getBasePrice());
        dto.setCapacity(entity.getCapacity());

        mapAuditFieldsToDto(entity, dto);

        return dto;
    }

    @Override
    public HotelRoomType toEntity(HotelRoomTypeDTO dto) {
        if (dto == null) {
            return null;
        }

        HotelRoomType entity = new HotelRoomType();
        entity.setId(dto.getId());

        Optional<Hotel> hotelOptional = hotelRepository.findById(dto.getHotelId());
        if (hotelOptional.isPresent()) {
            entity.setHotel(hotelOptional.get());
        } else {
            entity.setHotel(null);
        }

        entity.setName(dto.getName());
        entity.setBasePrice(dto.getBasePrice());
        entity.setCapacity(dto.getCapacity());

        mapAuditFieldsToEntity(dto, entity);

        return entity;
    }
}
