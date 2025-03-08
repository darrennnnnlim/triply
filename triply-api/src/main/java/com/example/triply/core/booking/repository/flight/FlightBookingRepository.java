package com.example.triply.core.booking.repository.flight;

import com.example.triply.core.booking.entity.flight.FlightBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightBookingRepository extends JpaRepository<FlightBooking, Long> {
    @Query("SELECT fb FROM FlightBooking fb WHERE fb.flight.id IN :flightIds")
    List<FlightBooking> findByFlightIdIn(List<Long> flightIds);
}
