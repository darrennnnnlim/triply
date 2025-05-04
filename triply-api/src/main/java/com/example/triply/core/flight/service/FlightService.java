package com.example.triply.core.flight.service;


import com.example.triply.core.booking.dto.FlightResponse;
import com.example.triply.core.flight.model.entity.Flight;
import com.example.triply.core.flight.repository.FlightRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class FlightService{

    private final FlightRepository flightRepository;


    @Autowired
    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public FlightResponse getFlightById(Long flightId){
        Optional<Flight> flight = flightRepository.findById(flightId);
        FlightResponse flightResponse = new FlightResponse();
        if (flight.isPresent()) {
            flightResponse.setId(flight.get().getId());
            flightResponse.setFlightNumber(flight.get().getFlightNumber());
            flightResponse.setAirline(flight.get().getAirline().getCode());
            flightResponse.setArrivalTime(flight.get().getArrivalTime());
            flightResponse.setDepartureTime(flight.get().getDepartureTime());

        } else {
            flightResponse.setId(null);
            flightResponse.setFlightNumber("N/A");
            flightResponse.setAirline("Unknown");
            flightResponse.setArrivalTime(null);
            flightResponse.setDepartureTime(null);
        }

        return flightResponse;

    }


}
