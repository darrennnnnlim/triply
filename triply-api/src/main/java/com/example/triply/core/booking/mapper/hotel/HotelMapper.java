package com.example.triply.core.booking.mapper.hotel;

import com.example.triply.common.mapper.BaseMapper;
import com.example.triply.core.booking.dto.hotel.HotelDTO;
import com.example.triply.core.booking.entity.hotel.Hotel;
import org.springframework.stereotype.Component;

@Component
public class HotelMapper implements BaseMapper<Hotel, HotelDTO> {
    @Override
    public HotelDTO toDto(Hotel entity) {
        if (entity == null) {
            return null;
        }

        HotelDTO dto = new HotelDTO();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLocation(entity.getLocation());
        dto.setDescription(entity.getDescription());

        mapAuditFieldsToDto(entity, dto);

        return dto;
    }

    @Override
    public Hotel toEntity(HotelDTO dto) {
        if (dto == null) {
            return null;
        }

        Hotel entity = new Hotel();

        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setLocation(dto.getLocation());
        entity.setDescription(dto.getDescription());

        mapAuditFieldsToEntity(dto, entity);

        return entity;
    }
}
