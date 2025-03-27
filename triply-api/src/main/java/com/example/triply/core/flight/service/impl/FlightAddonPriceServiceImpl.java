package com.example.triply.core.flight.service.impl;

import com.example.triply.core.flight.model.entity.FlightAddonPrice;
import com.example.triply.core.flight.repository.flight.FlightAddonPriceRepository;
import com.example.triply.core.flight.service.FlightAddonPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightAddonPriceServiceImpl implements FlightAddonPriceService {

    private final FlightAddonPriceRepository flightAddonPriceRepository;

    @Autowired
    public FlightAddonPriceServiceImpl(FlightAddonPriceRepository flightAddonPriceRepository) {
        this.flightAddonPriceRepository = flightAddonPriceRepository;
    }

    public List<FlightAddonPrice> findFlightAddonPrices(Long flightId, List<Long> flightAddonIds) {
        return flightAddonPriceRepository.findByFlightAndFlightAddonIn(flightId, flightAddonIds);
    }
}
