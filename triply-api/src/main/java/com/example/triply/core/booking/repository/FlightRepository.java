package com.example.triply.core.booking.repository;


import com.example.triply.core.booking.entity.flight.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository extends JpaRepository<Flight, Long> {
}
