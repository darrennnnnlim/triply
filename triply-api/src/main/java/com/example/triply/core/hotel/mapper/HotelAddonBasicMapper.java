package com.example.triply.core.hotel.mapper;

import com.example.triply.common.mapper.BaseMapper;
import com.example.triply.core.hotel.model.dto.HotelAddonBasicDTO;
import com.example.triply.core.hotel.model.entity.HotelAddon;
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
