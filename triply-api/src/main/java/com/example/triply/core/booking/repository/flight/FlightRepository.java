package com.example.triply.core.booking.repository.flight;

import com.example.triply.core.booking.entity.flight.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    Optional<Flight> findById(Long id);
}
