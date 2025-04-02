package com.example.triply.core.flight.mapper;

import com.example.triply.common.mapper.BaseMapper;
import com.example.triply.core.flight.model.dto.FlightPriceDTO;
import com.example.triply.core.flight.model.entity.FlightPrice;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FlightPriceMapper implements BaseMapper<FlightPrice, FlightPriceDTO> {

    @Override
    public FlightPriceDTO toDto(FlightPrice entity) {
        FlightPriceDTO flightPriceDTO = new FlightPriceDTO();
        flightPriceDTO.setFlight(entity.getFlight());
        flightPriceDTO.setFlightClass(entity.getFlightClass());
        flightPriceDTO.setId(entity.getId());
        flightPriceDTO.setDepartureDate(entity.getDepartureDate());
        flightPriceDTO.setBasePrice(entity.getBasePrice());
        flightPriceDTO.setDiscount(entity.getDiscount());
        flightPriceDTO.setSurgeMultiplier(entity.getSurgeMultiplier());

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
