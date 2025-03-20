package com.example.triply.core.booking.dto;

import com.example.triply.common.dto.MutableDTO;
import com.example.triply.core.booking.dto.hotel.HotelDTO;
import com.example.triply.core.booking.dto.hotel.HotelRoomTypeBasicDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class HotelBookingDTO extends MutableDTO {
    private Long id;
    private Long hotelId;
    private Long hotelRoomTypeId;
    private Long bookingId;
    private Long userId;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;

    private HotelDTO hotel;
    private HotelRoomTypeBasicDTO hotelRoomType;
}
