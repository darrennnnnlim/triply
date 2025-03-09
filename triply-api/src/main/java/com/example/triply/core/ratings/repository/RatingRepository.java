package com.example.triply.core.ratings.repository;
import com.example.triply.core.ratings.entity.Ratings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  RatingRepository extends JpaRepository<Ratings, Long> {

}
