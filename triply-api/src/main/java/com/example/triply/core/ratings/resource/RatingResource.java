package com.example.triply.core.ratings.resource;

import com.example.triply.core.ratings.dto.RatingRequest;
import com.example.triply.core.ratings.dto.RatingResponse;
import com.example.triply.core.ratings.entity.Ratings;
import com.example.triply.core.ratings.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/${triply.api-version}/ratings")
public class RatingResource {
    @Autowired
    private RatingService ratingService;

    @PostMapping("/test")
    public ResponseEntity<?> postTest() {
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }

    @GetMapping("/test")
    public ResponseEntity<?> getTest() {
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }

    @GetMapping("/allRatings")
    public ResponseEntity<?> getAllRatings() {
        List<RatingResponse> ratings = ratingService.getAllRatings();
        if (ratings.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No ratings found");
        }

        return ResponseEntity.status(HttpStatus.OK).body(ratings);
    }

    @PostMapping("/submitRating")
    public ResponseEntity<RatingResponse> postRating(@RequestBody RatingRequest ratingRequest) {
        RatingResponse ratingResponse = ratingService.saveRating(ratingRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ratingResponse);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RatingResponse>> getRatingsByUserId(@PathVariable Long userId) {
        List<RatingResponse> ratings = ratingService.getRatingsByUserId(userId);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/flight/{flightId}")
    public ResponseEntity<List<RatingResponse>> getRatingsByFlightId(@PathVariable Long flightId) {
        List<RatingResponse> ratings = ratingService.getRatingsByFlightId(flightId);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<RatingResponse>> getRatingsByHotelId(@PathVariable Long hotelId) {
        List<RatingResponse> ratings = ratingService.getRatingsByHotelId(hotelId);
        return ResponseEntity.ok(ratings);
    }

}
