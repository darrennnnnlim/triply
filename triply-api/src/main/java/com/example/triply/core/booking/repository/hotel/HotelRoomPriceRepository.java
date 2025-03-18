package com.example.triply.core.booking.repository.hotel;

import com.example.triply.core.booking.entity.hotel.HotelRoomPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HotelRoomPriceRepository extends JpaRepository<HotelRoomPrice, Long> {

    @Query("SELECT hrp FROM HotelRoomPrice hrp WHERE hrp.hotelRoomType.id = :hotelRoomTypeId AND NOT (hrp.endDate < :checkIn OR hrp.startDate > :checkOut)")
    List<HotelRoomPrice> findOverlappingPrices(Long hotelRoomTypeId, LocalDateTime checkIn, LocalDateTime checkOut);

}
