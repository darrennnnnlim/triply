package com.example.triply.core.flight.service.impl;

import com.example.triply.core.flight.mapper.FlightAddonMapper;
import com.example.triply.core.flight.model.dto.FlightAddonDTO;
import com.example.triply.core.flight.model.dto.FlightAddonResponse;
import com.example.triply.core.flight.model.entity.FlightAddon;
import com.example.triply.core.flight.repository.FlightAddonRepository;
import com.example.triply.core.flight.service.FlightAddonService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FlightAddonServiceImpl implements FlightAddonService {

    private final FlightAddonRepository flightAddonRepository;

    private final FlightAddonMapper flightAddonMapper;

    public FlightAddonServiceImpl(FlightAddonRepository flightAddonRepository, FlightAddonMapper flightAddonMapper) {
        this.flightAddonRepository = flightAddonRepository;
        this.flightAddonMapper = flightAddonMapper;
    }

    @Override
    public List<FlightAddonResponse> getFlightAddonsByFlightId(Long flightId) {

        return flightAddonRepository.findFlightAddonByFlightId(flightId).stream()
                .map(objArr -> {
                    FlightAddon flightAddon = (FlightAddon) objArr[0];
                    BigDecimal price = (BigDecimal) objArr[1];

                    FlightAddonDTO flightAddonDTO = flightAddonMapper.toDto(flightAddon);

                    FlightAddonResponse response = new FlightAddonResponse();
                    response.setFlightAddonDTO(flightAddonDTO); // assuming one per response
                    response.setPrice(price);

                    return response;
                })
                .toList();
    }
}
