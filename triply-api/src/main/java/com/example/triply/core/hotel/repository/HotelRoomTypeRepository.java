package com.example.triply.core.hotel.repository;

import com.example.triply.core.hotel.model.entity.HotelRoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRoomTypeRepository extends JpaRepository<HotelRoomType, Long> {
}
