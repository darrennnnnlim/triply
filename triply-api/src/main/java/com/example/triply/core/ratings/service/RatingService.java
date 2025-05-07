package com.example.triply.core.ratings.service;
import com.example.triply.common.exception.RatingsNotFoundException;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.auth.repository.UserRepository;
import com.example.triply.core.booking.entity.flight.FlightBooking;
import com.example.triply.core.booking.entity.hotel.HotelBooking;
import com.example.triply.core.booking.repository.flight.FlightBookingRepository;
import com.example.triply.core.booking.repository.hotel.HotelBookingRepository;
import com.example.triply.core.flight.model.entity.Flight;
import com.example.triply.core.flight.repository.FlightRepository;
import com.example.triply.core.ratings.dto.RatingRequest;
import com.example.triply.core.ratings.dto.RatingResponse;
import com.example.triply.core.ratings.factory.RatingFactory;
import com.example.triply.core.ratings.repository.RatingRepository;
import com.example.triply.core.ratings.entity.Ratings;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final FlightBookingRepository flightBookingRepository;
    private final HotelBookingRepository hotelBookingRepository;
    private final FlightRepository flightRepository;

    public static final String FLIGHT = "Flight";
    private static final String RATINGS_NOT_FOUND_MESSAGE = "Ratings not found for the provided criteria";
    private static final String USER_NOT_FOUND_MESSAGE = "User not found";

    public RatingService(UserRepository userRepository,
                         FlightBookingRepository flightBookingRepository,
                         HotelBookingRepository hotelBookingRepository,
                         RatingRepository ratingRepository,
                         FlightRepository flightRepository) {
        this.userRepository = userRepository;
        this.flightBookingRepository = flightBookingRepository;
        this.hotelBookingRepository = hotelBookingRepository;
        this.ratingRepository = ratingRepository;
        this.flightRepository = flightRepository;
    }

    public RatingResponse saveRating(RatingRequest ratingRequest) {
        User user = userRepository.findById(ratingRequest.getUserId())
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND_MESSAGE));

        FlightBooking flightBooking = null;
        HotelBooking hotelBooking = null;

        if (FLIGHT.equalsIgnoreCase(ratingRequest.getType())) {
            flightBooking = flightBookingRepository.findById(ratingRequest.getFlightId())
                    .orElseThrow(() -> new RuntimeException("Flight Booking not found"));
        } else {
            hotelBooking = hotelBookingRepository.findById(ratingRequest.getHotelId())
                    .orElseThrow(() -> new RuntimeException("Hotel Booking not found"));
        }

        Ratings existingRating = null;
        if (FLIGHT.equalsIgnoreCase(ratingRequest.getType())) {
            existingRating = ratingRepository.findByUserAndFlightBooking(user, flightBooking);
        } else {
            existingRating = ratingRepository.findByUserAndHotelBooking(user, hotelBooking);
        }

        Ratings ratings;
        if (existingRating != null) {
            ratings = existingRating;
            ratings.setRating(ratingRequest.getRating());
        } else {
            ratings = RatingFactory.createRating(ratingRequest, user, flightBooking, hotelBooking);
        }

        Ratings savedRating = ratingRepository.save(ratings);

        RatingResponse ratingResponse = new RatingResponse();
        ratingResponse.setId(savedRating.getId());
        ratingResponse.setRating(savedRating.getRating());
        ratingResponse.setUserId(savedRating.getUser().getId());
        ratingResponse.setDelete(savedRating.getDelete());

        if (FLIGHT.equalsIgnoreCase(ratingRequest.getType())) {
            ratingResponse.setFlightId(savedRating.getFlightBooking().getId());
            ratingResponse.setHotelId(null);
        } else {
            ratingResponse.setFlightId(null);
            ratingResponse.setHotelId(savedRating.getHotelBooking().getId());
        }

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
            resp.setDelete(rating.getDelete());
            if (rating.getFlightBooking() == null) {
                resp.setFlightId(null);
                resp.setHotelId(rating.getHotelBooking().getHotel().getId());
            } else {
                resp.setFlightId(rating.getFlightBooking().getFlight().getId());
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
            resp.setDelete(rating.getDelete());
            if (rating.getFlightBooking() == null) {
                resp.setFlightId(null);
                resp.setHotelId(rating.getHotelBooking().getId());
            } else {
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
            resp.setDelete(rating.getDelete());
            if (rating.getFlightBooking() == null) {
                resp.setFlightId(null);
                resp.setHotelId(rating.getHotelBooking().getId());
            } else {
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
            resp.setDelete(rating.getDelete());
            if (rating.getFlightBooking() == null) {
                resp.setFlightId(null);
                resp.setHotelId(rating.getHotelBooking().getId());
            } else {
                resp.setFlightId(rating.getFlightBooking().getId());
                resp.setHotelId(null);
            }
            ratingResponses.add(resp);
        }
        return ratingResponses;
    }

    public void softDelete(Long userId, Long flightId, Long hotelId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException(USER_NOT_FOUND_MESSAGE));

        Ratings ratings = null;

        if (flightId != null) {
            FlightBooking flightBooking = flightBookingRepository.findById(flightId)
                    .orElseThrow(() -> new RuntimeException("FlightBooking not found"));

            ratings = ratingRepository.findByUserAndFlightBooking(user, flightBooking);
        } else if (hotelId != null) {
            HotelBooking hotelBooking = hotelBookingRepository.findById(hotelId)
                    .orElseThrow(() -> new RuntimeException("HotelBooking not found"));

            ratings = ratingRepository.findByUserAndHotelBooking(user, hotelBooking);
        }

        if (ratings == null) {
            throw new RatingsNotFoundException(RATINGS_NOT_FOUND_MESSAGE);
        }

        if ("F".equals(ratings.getDelete())) {
            ratings.setDelete("T");
        } else {
            ratings.setDelete("F");
        }
        ratingRepository.save(ratings);
    }

    public void softDeleteAllBy(Long userId) {

        userRepository.findById(userId).orElseThrow(() -> new RuntimeException(USER_NOT_FOUND_MESSAGE));

        List<Ratings> ratings = null;
        ratings = ratingRepository.findByUserId(userId);

        for (Ratings rating : ratings) {
            if (rating == null) {
                throw new RatingsNotFoundException(RATINGS_NOT_FOUND_MESSAGE);
            }

            if ("F".equals(rating.getDelete())) {
                rating.setDelete("T");
            }
        }
        ratingRepository.saveAll(ratings);
    }

    public void undoSoftDeleteAllBy(Long userId) {

        userRepository.findById(userId).orElseThrow(() -> new RuntimeException(USER_NOT_FOUND_MESSAGE));

        List<Ratings> ratings = null;
        ratings = ratingRepository.findByUserId(userId);

        for (Ratings rating : ratings) {
            if (rating == null) {
                throw new RatingsNotFoundException(RATINGS_NOT_FOUND_MESSAGE);
            }

            if ("T".equals(rating.getDelete())) {
                rating.setDelete("F");
            }
        }
        ratingRepository.saveAll(ratings);
    }

    public List<RatingResponse> getRatingsByAirlineId(Long airlineId) {
        List<Long> flightsWithAirlineId = flightRepository.findAllByAirlineId(airlineId).stream().map(Flight::getId).toList();
        List<Long> flightBookingsWithAirlineId = flightBookingRepository.findAllByFlightIdIn(flightsWithAirlineId).stream().map(FlightBooking::getId).toList();
        List<Ratings> ratings = ratingRepository.findAllByFlightBookingIdIn(flightBookingsWithAirlineId);
        List<RatingResponse> ratingResponses = new ArrayList<>();
        for (Ratings rating : ratings) {
            RatingResponse resp = new RatingResponse();
            resp.setId(rating.getId());
            resp.setRating(rating.getRating());
            resp.setUserId(rating.getUser().getId());
            resp.setDelete(rating.getDelete());
            if (rating.getFlightBooking() == null) {
                resp.setFlightId(null);
                resp.setHotelId(rating.getHotelBooking().getId());
            } else {
                resp.setFlightId(rating.getFlightBooking().getId());
                resp.setAirlineId(airlineId);
                resp.setHotelId(null);
            }
            ratingResponses.add(resp);
        }
        return ratingResponses;
    }
}
