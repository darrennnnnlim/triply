package com.example.triply.core.booking.repository.flight;

import com.example.triply.core.booking.entity.flight.FlightBookingAddon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightBookingAddonRepository extends JpaRepository<FlightBookingAddon, Long> {
    @Query("SELECT fba FROM FlightBookingAddon fba WHERE fba.flightBooking.id IN :flightBookingId")
    List<FlightBookingAddon> findByFlightBookingIdIn(List<Long> flightBookingId);
}
