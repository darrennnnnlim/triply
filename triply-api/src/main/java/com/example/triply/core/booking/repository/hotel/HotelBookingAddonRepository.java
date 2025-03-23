package com.example.triply.core.booking.repository.hotel;

import com.example.triply.core.booking.entity.hotel.HotelBookingAddon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelBookingAddonRepository extends JpaRepository<HotelBookingAddon, Long> {
    @Query("SELECT hba FROM HotelBookingAddon hba WHERE hba.hotelBooking.id IN (:hotelBookingIds)")
    List<HotelBookingAddon> findByHotelBookingIdIn(List<Long> hotelBookingIds);
}
