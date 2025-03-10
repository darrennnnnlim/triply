package com.example.triply.core.booking.repository;

import com.example.triply.core.booking.entity.hotel.HotelBooking;
import org.springframework.data.jpa.repository.JpaRepository;


public interface HotelBookingRepository extends JpaRepository<HotelBooking, Long> {
}
