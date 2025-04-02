package com.example.triply.core.flight.service;

import com.example.triply.core.flight.model.entity.FlightAddonPrice;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FlightAddonPriceService {

    public List<FlightAddonPrice> findFlightAddonPrices(Long flightId, List<Long> flightAddonIds);
}
