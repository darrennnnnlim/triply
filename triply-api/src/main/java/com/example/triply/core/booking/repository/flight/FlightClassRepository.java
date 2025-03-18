package com.example.triply.core.booking.repository.flight;

import com.example.triply.core.booking.entity.flight.FlightClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FlightClassRepository extends JpaRepository<FlightClass, Long> {

    Optional<FlightClass> findById(Long aLong);
}
