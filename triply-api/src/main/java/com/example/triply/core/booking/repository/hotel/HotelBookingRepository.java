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

    @Query("SELECT hb FROM HotelBooking hb INNER JOIN Booking b ON b.id=hb.booking.id WHERE b.user.id = :userId")
    List<HotelBooking> findByUserId(Long userId);

    @Query("SELECT hb FROM HotelBooking hb WHERE hb.booking.id = :bookingId")
    List<HotelBooking> findByBookingId(Long bookingId);

    @Query("SELECT hb FROM HotelBooking hb WHERE hb.booking.id IN :bookingIds")
    List<HotelBooking> findByBookingIdsIn(List<Long> bookingIds);
}
