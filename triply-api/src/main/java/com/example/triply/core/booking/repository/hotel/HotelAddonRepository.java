package com.example.triply.core.booking.repository.hotel;

import com.example.triply.core.booking.entity.hotel.HotelAddon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelAddonRepository extends JpaRepository<HotelAddon, Long> {
    @Query("SELECT ha FROM HotelAddon ha WHERE ha.id IN (:ids)")
    List<HotelAddon> findByIdIn(List<Long> ids);
}
