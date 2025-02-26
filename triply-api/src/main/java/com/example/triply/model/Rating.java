package com.example.triply.model;

public class Rating {
    private String userId;
    private int rating;
    private String flightHotelId;

    public Rating(String userId, int rating, String flightHotelId) {
        this.userId = userId;
        this.rating = rating; 
        this.flightHotelId = flightHotelId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getFlightHotelId() {
        return flightHotelId;
    }

    public void setFlightHotelId(String flightHotelId) {
        this.flightHotelId = flightHotelId;
    }
    
}
