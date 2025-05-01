package com.example.triply.core.ratings.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;

@Data
public class RatingResponse {

    @NotNull
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private int rating;


    @Null
    private Long flightId;

    @Null
    private Long hotelId;

    @Null
    private Long airlineId;

    @Null
    private String delete;


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

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public String getDelete() {
        return delete;
    }

    public void setDelete(String delete) {
        this.delete = delete;
    }


}
