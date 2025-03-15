package com.example.triply.core.booking.repository;

import com.example.triply.core.booking.entity.flight.FlightBooking;
import com.example.triply.core.booking.entity.hotel.Hotel;
import com.example.triply.core.booking.entity.hotel.HotelBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface HotelBookingRepository extends JpaRepository<HotelBooking, Long> {
    @Query("SELECT hb FROM HotelBooking hb INNER JOIN Booking b ON b.id=hb.booking.id WHERE b.user.id = :userId")
    List<HotelBooking> findByUserId(Long userId);

    @Query("SELECT hb FROM HotelBooking hb WHERE hb.booking.id = :bookingId")
    List<HotelBooking> findByBookingId(Long bookingId);
}
