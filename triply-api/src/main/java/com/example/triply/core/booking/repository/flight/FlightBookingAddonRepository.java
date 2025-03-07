package com.example.triply.core.booking.repository.flight;

import com.example.triply.core.booking.entity.flight.FlightBookingAddon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightBookingAddonRepository extends JpaRepository<FlightBookingAddon, Long> {
}
