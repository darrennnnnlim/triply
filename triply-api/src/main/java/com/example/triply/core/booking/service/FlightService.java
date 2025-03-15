package com.example.triply.core.booking.service;


import com.example.triply.core.booking.dto.FlightRequest;
import com.example.triply.core.booking.dto.FlightResponse;
import com.example.triply.core.booking.entity.Booking;
import com.example.triply.core.booking.entity.flight.Flight;
import com.example.triply.core.booking.repository.FlightRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class FlightService{
    @Autowired
    private FlightRepository flightRepository;

    public FlightResponse getFlightById(Long flightId){
        Optional<Flight> flight = flightRepository.findById(flightId);
        FlightResponse flightResponse = new FlightResponse();
        flightResponse.setId(flight.get().getId());
        flightResponse.setFlightNumber(flight.get().getFlightNumber());
        flightResponse.setAirline(flight.get().getAirline());
        flightResponse.setArrival(flight.get().getArrival());
        flightResponse.setDeparture(flight.get().getDeparture());
        flightResponse.setArrivalTime(flight.get().getArrivalTime());
        flightResponse.setDepartureTime(flight.get().getDepartureTime());
        flightResponse.setBasePrice(flight.get().getBasePrice());
        return flightResponse;

    }


}
