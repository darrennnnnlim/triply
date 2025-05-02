package com.example.triply.core.flight.service;

import com.example.triply.core.flight.model.dto.FlightAddonResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FlightAddonService {
    public List<FlightAddonResponse> getFlightAddonsByFlightId(Long flightId);
}
