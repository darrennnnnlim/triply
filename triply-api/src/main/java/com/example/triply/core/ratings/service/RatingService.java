package com.example.triply.core.ratings.service;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.auth.repository.UserRepository;
import com.example.triply.core.booking.entity.flight.FlightBooking;
import com.example.triply.core.booking.entity.hotel.HotelBooking;
import com.example.triply.core.booking.repository.flight.FlightBookingRepository;
import com.example.triply.core.booking.repository.hotel.HotelBookingRepository;
import com.example.triply.core.ratings.dto.RatingRequest;
import com.example.triply.core.ratings.dto.RatingResponse;
import com.example.triply.core.ratings.repository.RatingRepository;
import com.example.triply.core.ratings.entity.Ratings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

@Service
public class RatingService {
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FlightBookingRepository flightBookingRepository;

    @Autowired
    private HotelBookingRepository hotelBookingRepository;


    public RatingResponse saveRating(RatingRequest ratingRequest) {


        User user = userRepository.findById(ratingRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));


        FlightBooking flightBooking = null;
        HotelBooking hotelBooking = null;

        if ("Flight".equalsIgnoreCase(ratingRequest.getType())) {
            flightBooking = flightBookingRepository.findById(ratingRequest.getFlightId())
                    .orElseThrow(() -> new RuntimeException("Flight Booking not found"));
        } else {
            hotelBooking = hotelBookingRepository.findById(ratingRequest.getHotelId())
                    .orElseThrow(() -> new RuntimeException("Hotel Booking not found"));
        }


        Ratings existingRating = null;
        if ("Flight".equalsIgnoreCase(ratingRequest.getType())) {
            existingRating = ratingRepository.findByUserAndFlightBooking(user, flightBooking);
        } else {
            existingRating = ratingRepository.findByUserAndHotelBooking(user, hotelBooking);
        }

        Ratings ratings;
        if (existingRating != null) {
            // Update Rating
            ratings = existingRating;
            ratings.setRating(ratingRequest.getRating());
        } else {
//            Submit Rating
            ratings = new Ratings();
            ratings.setRating(ratingRequest.getRating());
            ratings.setUser(user);
            ratings.setFlightBooking(flightBooking);
            ratings.setHotelBooking(hotelBooking);
        }

        Ratings savedRating = ratingRepository.save(ratings);

        RatingResponse ratingResponse = new RatingResponse();
        ratingResponse.setId(ratings.getId());
        ratingResponse.setRating(ratings.getRating());
        ratingResponse.setUserId(ratings.getUser().getId());


        if ("Flight".equalsIgnoreCase(ratingRequest.getType())) {
            ratingResponse.setFlightId(ratings.getFlightBooking().getId());
            ratingResponse.setHotelId(null);
        } else {
            ratingResponse.setFlightId(null);
            ratingResponse.setHotelId(ratings.getHotelBooking().getId());
        }





        ratingResponse.setId(savedRating.getId());

        return ratingResponse;
    }

    public List<RatingResponse> getAllRatings() {
        List<Ratings> ratings = ratingRepository.findAll();
        List<RatingResponse> ratingResponses = new ArrayList<>();
        for (Ratings rating : ratings) {
            RatingResponse resp = new RatingResponse();
            resp.setId(rating.getId());
            resp.setRating(rating.getRating());
            resp.setUserId(rating.getUser().getId());
            if(rating.getFlightBooking() == null){
                resp.setFlightId(null);
                resp.setHotelId(rating.getHotelBooking().getId());
            }
            else{
                resp.setFlightId(rating.getFlightBooking().getId());
                resp.setHotelId(null);
            }
            ratingResponses.add(resp);
        }
        return ratingResponses;
    }

    public List<RatingResponse> getRatingsByUserId(Long userId) {
        List<Ratings> ratings = ratingRepository.findByUserId(userId);
        List<RatingResponse> ratingResponses = new ArrayList<>();
        for (Ratings rating : ratings) {
            RatingResponse resp = new RatingResponse();
            resp.setId(rating.getId());
            resp.setRating(rating.getRating());
            resp.setUserId(rating.getUser().getId());
            if(rating.getFlightBooking() == null){
                resp.setFlightId(null);
                resp.setHotelId(rating.getHotelBooking().getId());
            }
            else{
                resp.setFlightId(rating.getFlightBooking().getId());
                resp.setHotelId(null);
            }
            ratingResponses.add(resp);
        }
        return ratingResponses;
    }

    public List<RatingResponse> getRatingsByFlightId(Long flightId) {

        List<Ratings> ratings = ratingRepository.findByFlightId(flightId);
        List<RatingResponse> ratingResponses = new ArrayList<>();
        for (Ratings rating : ratings) {
            RatingResponse resp = new RatingResponse();
            resp.setId(rating.getId());
            resp.setRating(rating.getRating());
            resp.setUserId(rating.getUser().getId());
            if(rating.getFlightBooking() == null){
                resp.setFlightId(null);
                resp.setHotelId(rating.getHotelBooking().getId());
            }
            else{
                resp.setFlightId(rating.getFlightBooking().getId());
                resp.setHotelId(null);
            }
            ratingResponses.add(resp);
        }
        return ratingResponses;
    }

    public List<RatingResponse> getRatingsByHotelId(Long hotelId) {

        List<Ratings> ratings = ratingRepository.findByHotelId(hotelId);
        List<RatingResponse> ratingResponses = new ArrayList<>();
        for (Ratings rating : ratings) {
            RatingResponse resp = new RatingResponse();
            resp.setId(rating.getId());
            resp.setRating(rating.getRating());
            resp.setUserId(rating.getUser().getId());
            if(rating.getFlightBooking() == null){
                resp.setFlightId(null);
                resp.setHotelId(rating.getHotelBooking().getId());
            }
            else{
                resp.setFlightId(rating.getFlightBooking().getId());
                resp.setHotelId(null);
            }
            ratingResponses.add(resp);
        }
        return ratingResponses;
    }
}
