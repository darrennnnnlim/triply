package com.example.triply.core.ratings.repository;
import com.example.triply.core.ratings.entity.Ratings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface  RatingRepository extends JpaRepository<Ratings, Long> {

    @Query("SELECT r FROM Ratings r WHERE r.user.id = :userId")
    List<Ratings> findByUserId(Long userId);

    @Query("SELECT r FROM Ratings r WHERE r.flightHotelId = :flightHotelId")
    List<Ratings> findByFlightHotelId(Long flightHotelId);
}
