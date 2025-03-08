package com.example.triply.core.booking.repository.hotel;

import com.example.triply.core.booking.entity.hotel.HotelBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelBookingRepository extends JpaRepository<HotelBooking, Long> {
    @Query("SELECT hb FROM HotelBooking hb WHERE hb.hotel.id IN (:hotelIds)")
    List<HotelBooking> findByHotelIdIn(List<Long> hotelIds);
}
