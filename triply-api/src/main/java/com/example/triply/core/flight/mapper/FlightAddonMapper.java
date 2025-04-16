package com.example.triply.core.flight.mapper;

import com.example.triply.common.mapper.BaseMapper;
import com.example.triply.core.flight.model.dto.FlightAddonDTO;
import com.example.triply.core.flight.model.entity.FlightAddon;
import org.springframework.stereotype.Component;

@Component
public class FlightAddonMapper implements BaseMapper<FlightAddon, FlightAddonDTO> {
    @Override
    public FlightAddonDTO toDto(FlightAddon entity) {
        if (entity == null) {
            return null;
        }

        FlightAddonDTO dto = new FlightAddonDTO();

        dto.setId(entity.getId());
        dto.setName(entity.getName());

        mapAuditFieldsToDto(entity, dto);

        return dto;
    }

    @Override
    public FlightAddon toEntity(FlightAddonDTO dto) {
        if (dto == null) {
            return null;
        }

        FlightAddon entity = new FlightAddon();

        entity.setId(dto.getId());
        entity.setName(dto.getName());

        mapAuditFieldsToEntity(dto, entity);

        return entity;
    }
}
