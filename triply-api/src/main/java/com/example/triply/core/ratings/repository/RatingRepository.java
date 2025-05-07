package com.example.triply.core.ratings.repository;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.booking.entity.flight.FlightBooking;
import com.example.triply.core.booking.entity.hotel.HotelBooking;
import com.example.triply.core.ratings.entity.Ratings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface  RatingRepository extends JpaRepository<Ratings, Long> {

    @Query("SELECT r FROM Ratings r WHERE r.user.id = :userId")
    List<Ratings> findByUserId(Long userId);

    @Query("SELECT r FROM Ratings r WHERE r.flightBooking.id = :flightId")
    List<Ratings> findByFlightId(Long flightId);

    @Query("SELECT r FROM Ratings r WHERE r.hotelBooking.id = :hotelId")
    List<Ratings> findByHotelId(Long hotelId);

    Ratings findByUserAndFlightBooking(User user, FlightBooking flightBooking);

    Ratings findByUserIdAndFlightBookingId(Long userId, Long flightBookingId);

    Ratings findByUserAndHotelBooking(User user, HotelBooking hotelBooking);

    List<Ratings> findAllByFlightBookingIdIn(List<Long> flightsWithAirlineId);

    Ratings findByUserIdAndHotelBookingId(Long userId, Long flightId);
}
