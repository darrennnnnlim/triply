package com.example.triply.core.flight.repository;

import com.example.triply.core.flight.model.entity.FlightPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightPriceRepository extends JpaRepository<FlightPrice, Long>, JpaSpecificationExecutor<FlightPrice> {
    @Query("SELECT fp FROM FlightPrice fp WHERE fp.flight.id = :flightId AND fp.flightClass.id = :flightClassId")
    Optional<FlightPrice> findByFlightAndFlightClass(Long flightId, Long flightClassId);

    @Query("SELECT fp FROM FlightPrice fp WHERE fp.flight.id IN :flightIds " +
            "AND FUNCTION('DATE', fp.departureDate) = FUNCTION('DATE', :departureDate)")
    List<FlightPrice> findAllByFlightIdAndByDepartureDateIn(
            @Param("flightIds") List<Long> flightIds,
            @Param("departureDate") LocalDateTime departureDate);

}
