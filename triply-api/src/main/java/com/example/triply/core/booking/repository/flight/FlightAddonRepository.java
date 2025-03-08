package com.example.triply.core.booking.repository.flight;

import com.example.triply.core.booking.entity.flight.FlightAddon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FlightAddonRepository extends JpaRepository<FlightAddon, Long> {
    @Query("SELECT fa FROM FlightAddon fa WHERE fa.id = :id")
    Optional<FlightAddon> findFlightAddonById(Long id);
}
