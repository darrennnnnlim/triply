package com.example.triply.core.booking.repository.flight;

import com.example.triply.core.booking.entity.flight.FlightPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FlightPriceRepository extends JpaRepository<FlightPrice, Long> {
    @Query("SELECT fp FROM FlightPrice fp WHERE fp.flight.id = :flightId AND fp.flightClass.id = :flightClassId")
    Optional<FlightPrice> findByFlightAndFlightClass(Long flightId, Long flightClassId);
}
