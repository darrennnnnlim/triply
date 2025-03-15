package com.example.triply.core.booking.repository;

import com.example.triply.core.booking.entity.flight.FlightBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FlightBookingRepository extends JpaRepository<FlightBooking, Long>{

    @Query("SELECT fb FROM FlightBooking fb INNER JOIN Booking b ON b.id=fb.booking.id WHERE b.user.id = :userId")
    List<FlightBooking> findByUserId(Long userId);

    @Query("SELECT fb FROM FlightBooking fb WHERE fb.booking.id = :bookingId")
    List<FlightBooking> findByBookingId(Long bookingId);
}


