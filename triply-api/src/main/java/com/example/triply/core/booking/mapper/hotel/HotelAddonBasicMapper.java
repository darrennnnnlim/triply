package com.example.triply.core.booking.mapper.hotel;

import com.example.triply.common.mapper.BaseMapper;
import com.example.triply.core.booking.dto.hotel.HotelAddonBasicDTO;
import com.example.triply.core.booking.entity.hotel.HotelAddon;
import org.springframework.stereotype.Component;

@Component
public class HotelAddonBasicMapper implements BaseMapper<HotelAddon, HotelAddonBasicDTO> {

    @Override
    public HotelAddonBasicDTO toDto(HotelAddon entity) {
        if (entity == null) {
            return null;
        }

        HotelAddonBasicDTO dto = new HotelAddonBasicDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());

        mapAuditFieldsToDto(entity, dto);

        return dto;
    }

    @Override
    public HotelAddon toEntity(HotelAddonBasicDTO dto) {
        if (dto == null) {
            return null;
        }

        HotelAddon entity = new HotelAddon();
        entity.setId(dto.getId());
        entity.setName(dto.getName());

        mapAuditFieldsToEntity(dto, entity);

        return entity;
    }
}
