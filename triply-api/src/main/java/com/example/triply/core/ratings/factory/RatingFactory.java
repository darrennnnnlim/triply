package com.example.triply.core.ratings.factory;

import com.example.triply.core.auth.entity.User;
import com.example.triply.core.booking.entity.flight.FlightBooking;
import com.example.triply.core.booking.entity.hotel.HotelBooking;
import com.example.triply.core.ratings.dto.RatingRequest;
import com.example.triply.core.ratings.entity.Ratings;

public class RatingFactory {

    public static Ratings createRating(RatingRequest ratingRequest, User user, FlightBooking flightBooking, HotelBooking hotelBooking) {
        Ratings ratings = new Ratings();
        ratings.setRating(ratingRequest.getRating());
        ratings.setUser(user);
        ratings.setStatus("Submitted");
        ratings.setDelete("F");

        if ("Flight".equalsIgnoreCase(ratingRequest.getType())) {
            ratings.setFlightBooking(flightBooking);
        } else {
            ratings.setHotelBooking(hotelBooking);
        }

        return ratings;
    }
}
