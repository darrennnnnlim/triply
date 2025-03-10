package com.example.triply.core.booking.mapper.flight;

import com.example.triply.common.mapper.BaseMapper;
import com.example.triply.core.booking.dto.flight.AirlineDTO;
import com.example.triply.core.booking.entity.flight.Airline;
import org.springframework.stereotype.Component;

@Component
public class AirlineMapper implements BaseMapper<Airline, AirlineDTO> {
    @Override
    public AirlineDTO toDto(Airline entity) {
        if (entity == null) {
            return null;
        }

        AirlineDTO dto = new AirlineDTO();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCode(entity.getCode());

        mapAuditFieldsToDto(entity, dto);

        return dto;
    }

    @Override
    public Airline toEntity(AirlineDTO dto) {
        if (dto == null) {
            return null;
        }

        Airline entity = new Airline();

        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setCode(dto.getCode());

        mapAuditFieldsToEntity(dto, entity);

        return entity;
    }
}
