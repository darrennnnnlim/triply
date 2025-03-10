package com.example.triply.core.booking.repository;

import com.example.triply.core.booking.entity.flight.FlightBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlightBookingRepository extends JpaRepository<FlightBooking, Long>{
}


