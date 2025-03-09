package com.example.triply.core.ratings.service;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.auth.repository.UserRepository;
import com.example.triply.core.ratings.dto.RatingRequest;
import com.example.triply.core.ratings.dto.RatingResponse;
import com.example.triply.core.ratings.repository.RatingRepository;
import com.example.triply.core.ratings.entity.Ratings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RatingService {
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private UserRepository userRepository;

    public RatingResponse saveRating(RatingRequest ratingRequest) {

            User user = userRepository.findById(ratingRequest.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        Ratings ratings = new Ratings();
        ratings.setRating(ratingRequest.getRating());
        ratings.setUser(user);
        ratings.setFlightHotelId(ratingRequest.getFlightHotelId());

        Ratings savedRating = ratingRepository.save(ratings);

        RatingResponse ratingResponse = new RatingResponse();
        ratingResponse.setId(savedRating.getId());
        ratingResponse.setRating(savedRating.getRating());
        ratingResponse.setUserId(savedRating.getUser().getId());
        ratingResponse.setFlightHotelId(savedRating.getFlightHotelId());

        return ratingResponse;
    }


    public List<Ratings> getAllRatings() {
        return ratingRepository.findAll();
    }

    public List<Ratings> getRatingsByUserId(Long userId) {
        return ratingRepository.findByUserId(userId); // Using automatic query generation
    }

    public List<Ratings> getRatingsByFlightHotelId(Long flightHotelId) {
        return ratingRepository.findByFlightHotelId(flightHotelId); // Using automatic query generation
    }
}
