package com.example.triply.core.flight.mapper;

import com.example.triply.common.mapper.BaseMapper;
import com.example.triply.core.flight.model.dto.FlightDTO;
import com.example.triply.core.flight.model.entity.Airline;
import com.example.triply.core.flight.model.entity.Flight;
import com.example.triply.core.flight.repository.AirlineRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FlightMapper implements BaseMapper<Flight, FlightDTO> {

    private final AirlineRepository airlineRepository;

    public FlightMapper(AirlineRepository airlineRepository) {
        this.airlineRepository = airlineRepository;
    }

    @Override
    public FlightDTO toDto(Flight entity) {
        if (entity == null) {
            return null;
        }

        FlightDTO dto = new FlightDTO();
        dto.setId(entity.getId());
        dto.setAirlineId(entity.getAirline().getId());
        dto.setFlightNumber(entity.getFlightNumber());
        dto.setOrigin(entity.getOrigin());
        dto.setDestination(entity.getDestination());
        dto.setDepartureTime(entity.getDepartureTime());
        dto.setArrivalTime(entity.getArrivalTime());

        mapAuditFieldsToDto(entity, dto);

        return dto;
    }

    @Override
    public Flight toEntity(FlightDTO dto) {
        if (dto == null) {
            return null;
        }

        Flight entity = new Flight();
        entity.setId(dto.getId());

        Optional<Airline> airlineOptional = airlineRepository.findById(dto.getAirlineId());
        if (airlineOptional.isPresent()) {
            entity.setAirline(airlineOptional.get());
        } else {
            entity.setAirline(null);
        }

        entity.setFlightNumber(dto.getFlightNumber());
        entity.setOrigin(dto.getOrigin());
        entity.setDestination(dto.getDestination());
        entity.setDepartureTime(dto.getDepartureTime());
        entity.setArrivalTime(dto.getArrivalTime());

        mapAuditFieldsToEntity(dto, entity);

        return entity;
    }
}
