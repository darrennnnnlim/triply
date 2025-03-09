package com.example.triply.core.ratings.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RatingResponse {

    @NotNull
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private int rating;

    @NotBlank
    private Long flightHotelId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Long getFlightHotelId() {
        return flightHotelId;
    }

    public void setFlightHotelId(Long flightHotelId) {
        this.flightHotelId = flightHotelId;
    }



}
