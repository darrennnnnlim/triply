package com.example.triply.core.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class HotelBookingResponse {

    @NotNull
    private Long userId;

   @NotNull
    private Long hotelId;

    @NotNull
    private String checkIn;

    @NotNull
    private String checkOut;

    @NotNull
    private String roomType;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(String checkOut) {
        this.checkOut = checkOut;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

}
