package com.example.triply.core.booking.mapper.flight;

import com.example.triply.common.mapper.BaseMapper;
import com.example.triply.core.booking.dto.flight.FlightBookingAddonDTO;
import com.example.triply.core.booking.entity.flight.FlightAddon;
import com.example.triply.core.booking.entity.flight.FlightBooking;
import com.example.triply.core.booking.entity.flight.FlightBookingAddon;
import com.example.triply.core.booking.repository.flight.FlightAddonRepository;
import com.example.triply.core.booking.repository.flight.FlightBookingRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class FlightBookingAddonMapper implements BaseMapper<FlightBookingAddon, FlightBookingAddonDTO> {

    private final FlightBookingRepository flightBookingRepository;

    private final FlightAddonRepository flightAddonRepository;

    public FlightBookingAddonMapper(FlightBookingRepository flightBookingRepository, FlightAddonRepository flightAddonRepository) {
        this.flightBookingRepository = flightBookingRepository;
        this.flightAddonRepository = flightAddonRepository;
    }

    @Override
    public FlightBookingAddonDTO toDto(FlightBookingAddon entity) {
        if (entity == null) {
            return null;
        }

        FlightBookingAddonDTO dto = new FlightBookingAddonDTO();
        dto.setId(entity.getId());
        dto.setFlightBookingId(entity.getFlightBooking().getId());
        dto.setFlightAddonId(entity.getFlightAddon().getId());
        dto.setPrice(entity.getPrice());
        dto.setQuantity(entity.getQuantity());

        return dto;
    }

    @Override
    public FlightBookingAddon toEntity(FlightBookingAddonDTO dto) {
        if (dto == null) {
            return null;
        }

        FlightBookingAddon entity = new FlightBookingAddon();
        entity.setId(dto.getId());

        Optional<FlightBooking> flightBookingOptional = flightBookingRepository.findById(dto.getFlightBookingId());
        if (flightBookingOptional.isPresent()) {
            entity.setFlightBooking(flightBookingOptional.get());
        } else {
            entity.setFlightBooking(null);
        }

        Optional<FlightAddon> flightAddonOptional = flightAddonRepository.findFlightAddonById(dto.getFlightAddonId());
        if (flightAddonOptional.isPresent()) {
            entity.setFlightAddon(flightAddonOptional.get());
        } else {
            entity.setFlightAddon(null);
        }

        entity.setPrice(dto.getPrice());
        entity.setQuantity(dto.getQuantity());

        return entity;
    }

    @Override
    public List<FlightBookingAddonDTO> toDto(List<FlightBookingAddon> entities) {
        return List.of();
    }

    @Override
    public List<FlightBookingAddon> toEntity(List<FlightBookingAddonDTO> dto) {
        return dto.stream().map(this::toEntity).toList();
    }
}
