package com.example.triply.core.flight.repository;

import com.example.triply.core.flight.model.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    Optional<Flight> findById(Long id);

    @Query("SELECT f FROM Flight f WHERE f.origin = :originIATA AND f.destination = :destIATA " +
            "AND FUNCTION('DATE', f.departureTime) = FUNCTION('DATE', :departureTime) " +
            "AND FUNCTION('DATE', f.arrivalTime) = FUNCTION('DATE', :arrivalTime)")
    List<Flight> findByOriginAndDestinationAndDepartureTimeAndArrivalTime(
            @Param("originIATA") String originIATA,
            @Param("destIATA") String destIATA,
            @Param("departureTime") LocalDateTime departureTime,
            @Param("arrivalTime") LocalDateTime arrivalTime);

    @Query("SELECT f FROM Flight f WHERE f.origin = :originIATA AND f.destination = :destIATA " +
            "AND FUNCTION('DATE', f.departureTime) = FUNCTION('DATE', :departureTime) ")
    List<Flight> findByOriginAndDestinationAndDepartureTime(
            @Param("originIATA") String originIATA,
            @Param("destIATA") String destIATA,
            @Param("departureTime") LocalDateTime departureTime);

    @Query("SELECT f FROM Flight f WHERE f.origin = :originIATA AND f.destination = :destIATA " +
            "AND FUNCTION('DATE', f.arrivalTime) = FUNCTION('DATE', :arrivalTime)")
    List<Flight> findByOriginAndDestinationAndArrivalTime(
            @Param("originIATA") String originIATA,
            @Param("destIATA") String destIATA,
            @Param("arrivalTime") LocalDateTime arrivalTime);

    List<Flight> findAllByAirlineId(Long airlineId);
}
