package com.example.triply.core.flight.mapper;

import com.example.triply.common.mapper.BaseMapper;
import com.example.triply.core.flight.model.dto.FlightClassDTO;
import com.example.triply.core.flight.model.entity.Flight;
import com.example.triply.core.flight.model.entity.FlightClass;
import org.springframework.stereotype.Component;


@Component
public class FlightClassMapper implements BaseMapper<FlightClass, FlightClassDTO> {

    @Override
    public FlightClassDTO toDto(FlightClass entity) {
        if (entity == null) {
            return null;
        }

        FlightClassDTO dto = new FlightClassDTO();
        dto.setClassName(entity.getClassName());
        dto.setId(entity.getId());

        mapAuditFieldsToDto(entity, dto);

        return dto;
    }

    @Override
    public FlightClass toEntity(FlightClassDTO dto) {
        if (dto == null) {
            return null;
        }

        FlightClass entity = new FlightClass();
        entity.setId(dto.getId());
        entity.setClassName(dto.getClassName());

        mapAuditFieldsToEntity(dto, entity);

        return entity;
    }
}
