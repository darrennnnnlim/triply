package com.example.triply.core.hotel.repository;

import com.example.triply.core.hotel.model.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findAllByLocation(String location);
}
