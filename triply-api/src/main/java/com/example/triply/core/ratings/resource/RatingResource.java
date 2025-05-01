package com.example.triply.core.ratings.resource;

import com.example.triply.core.ratings.dto.RatingRequest;
import com.example.triply.core.ratings.dto.RatingResponse;
import com.example.triply.core.ratings.service.RatingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/${triply.api-version}/ratings")
public class RatingResource {

    private final RatingService ratingService;

    public RatingResource(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping("/allRatings")
    public ResponseEntity<Object> getAllRatings() {
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

    @PutMapping("/banRating/{userId}")
    public ResponseEntity<String> softDelete(@PathVariable Long userId, @RequestParam(required = false) Long flightId, @RequestParam(required = false) Long hotelId) {
        try {
            ratingService.softDelete(userId, flightId, hotelId);
            return ResponseEntity.ok("Soft delete successful.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/airline/{airlineId}")
    public ResponseEntity<List<RatingResponse>> getRatingsByAirlineId(@PathVariable Long airlineId) {
        List<RatingResponse> ratings = ratingService.getRatingsByAirlineId(airlineId);
        return ResponseEntity.ok(ratings);
    }
}
