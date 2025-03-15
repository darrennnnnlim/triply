package com.example.triply.core.booking.repository;

import com.example.triply.core.booking.entity.hotel.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
}
