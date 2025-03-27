package com.example.triply.core.flight.repository.flight;

import com.example.triply.core.flight.model.entity.FlightAddonPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightAddonPriceRepository extends JpaRepository<FlightAddonPrice, Long> {
    @Query("SELECT fap FROM FlightAddonPrice fap WHERE fap.flight.id = :flightId AND fap.flightAddon.id IN :flightAddonIds")
    List<FlightAddonPrice> findByFlightAndFlightAddonIn(Long flightId, List<Long> flightAddonIds);
}
