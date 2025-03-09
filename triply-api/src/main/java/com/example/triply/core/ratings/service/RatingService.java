package com.example.triply.core.ratings.service;
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

    public RatingResponse saveRating(RatingRequest ratingRequest) {
        Ratings ratings = new Ratings();
        ratings.setRating(ratingRequest.getRating());
        ratings.setUserId(ratingRequest.getUserId());
        ratings.setFlightHotelId(ratingRequest.getFlightHotelId());

        Ratings savedRating = ratingRepository.save(ratings);

        RatingResponse ratingResponse = new RatingResponse();
        ratingResponse.setRating(savedRating.getRating());
        ratingResponse.setUserId(savedRating.getUserId());
        ratingResponse.setFlightHotelId(savedRating.getFlightHotelId());

        return ratingResponse;
    }

    public Optional<Ratings> getRating(Long id) {
        return ratingRepository.findById(id);
    }

    public List<Ratings> getAllRatings() {
        return ratingRepository.findAll();
    }

    public List<Ratings> getRatingsByItemId(Long itemId) {
        return ratingRepository.findAll();
    }
}
