package com.example.triply.core.booking.repository.hotel;

import com.example.triply.core.booking.entity.hotel.HotelBookingAddon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelBookingAddonRepository extends JpaRepository<HotelBookingAddon, Long> {
}
