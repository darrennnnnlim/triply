package com.example.triply.core.flight.mapper;

import com.example.triply.common.mapper.BaseMapper;
import com.example.triply.core.flight.model.dto.FlightPriceDTO;
import com.example.triply.core.flight.model.entity.FlightPrice;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FlightPriceMapper implements BaseMapper<FlightPrice, FlightPriceDTO> {

    private FlightMapper flightMapper;
    private FlightClassMapper flightClassMapper;

    public FlightPriceMapper(FlightMapper flightMapper, FlightClassMapper flightClassMapper) {
        this.flightMapper = flightMapper;
        this.flightClassMapper = flightClassMapper;
    }

    @Override
    public FlightPriceDTO toDto(FlightPrice entity) {
        if (entity == null) {
            return null;
        }
        FlightPriceDTO flightPriceDTO = new FlightPriceDTO();
        flightPriceDTO.setId(entity.getId());
        flightPriceDTO.setDepartureDate(entity.getDepartureDate());
        flightPriceDTO.setBasePrice(entity.getBasePrice());
        flightPriceDTO.setDiscount(entity.getDiscount());
        flightPriceDTO.setSurgeMultiplier(entity.getSurgeMultiplier());

        flightPriceDTO.setFlightDTO(flightMapper.toDto(entity.getFlight()));
        flightPriceDTO.setFlightClassDTO(flightClassMapper.toDto(entity.getFlightClass()));

        // Populate primitive fields from related entities (handle nulls)
        if (entity.getFlight() != null) {
            flightPriceDTO.setFlightNumber(entity.getFlight().getFlightNumber());
            flightPriceDTO.setOrigin(entity.getFlight().getOrigin());
            flightPriceDTO.setDestination(entity.getFlight().getDestination());
        }
        if (entity.getFlightClass() != null) {
            flightPriceDTO.setFlightClassName(entity.getFlightClass().getClassName());
        }

        return flightPriceDTO;
    }

    @Override
    public FlightPrice toEntity(FlightPriceDTO dto) {
        return null;
    }

    @Override
    public List<FlightPriceDTO> toDto(List<FlightPrice> entities) {
        return List.of();
    }

    @Override
    public List<FlightPrice> toEntity(List<FlightPriceDTO> dto) {
        return List.of();
    }
}
