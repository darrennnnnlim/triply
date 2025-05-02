package com.example.triply.core.flight.repository;

import com.example.triply.core.flight.model.dto.FlightAddonResponse;
import com.example.triply.core.flight.model.entity.FlightAddon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlightAddonRepository extends JpaRepository<FlightAddon, Long> {
    @Query("SELECT fa FROM FlightAddon fa WHERE fa.id = :id")
    Optional<FlightAddon> findFlightAddonById(Long id);

    @Query("SELECT fa, fap.price FROM FlightAddon fa JOIN FlightAddonPrice fap ON fap.flightAddon.id = fa.id WHERE fap.flight.id = :flightId")
    List<Object[]> findFlightAddonByFlightId(Long flightId);
}
