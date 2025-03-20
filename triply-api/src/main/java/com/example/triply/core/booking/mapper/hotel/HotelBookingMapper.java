package com.example.triply.core.booking.mapper.hotel;

import com.example.triply.common.mapper.BaseMapper;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.auth.repository.UserRepository;
import com.example.triply.core.booking.dto.HotelBookingDTO;
import com.example.triply.core.booking.entity.Booking;
import com.example.triply.core.booking.entity.hotel.Hotel;
import com.example.triply.core.booking.entity.hotel.HotelBooking;
import com.example.triply.core.booking.entity.hotel.HotelRoomType;
import com.example.triply.core.booking.repository.BookingRepository;
import com.example.triply.core.booking.repository.hotel.HotelRepository;
import com.example.triply.core.booking.repository.hotel.HotelRoomTypeRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class HotelBookingMapper implements BaseMapper<HotelBooking, HotelBookingDTO> {

    private final HotelRepository hotelRepository;

    private final HotelRoomTypeRepository hotelRoomTypeRepository;

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    public HotelBookingMapper(HotelRepository hotelRepository, HotelRoomTypeRepository hotelRoomTypeRepository, BookingRepository bookingRepository, UserRepository userRepository) {
        this.hotelRepository = hotelRepository;
        this.hotelRoomTypeRepository = hotelRoomTypeRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @Override
    public HotelBookingDTO toDto(HotelBooking entity) {
        if (entity == null) {
            return null;
        }

        HotelBookingDTO dto = new HotelBookingDTO();
        dto.setId(entity.getId());
        dto.setHotelId(entity.getHotel().getId());
        dto.setHotelRoomTypeId(entity.getHotelRoomType().getId());
        dto.setBookingId(entity.getBooking().getId());
        dto.setUserId(entity.getUser().getId());
        dto.setCheckIn(entity.getCheckIn());
        dto.setCheckOut(entity.getCheckOut());

        mapAuditFieldsToDto(entity, dto);

        return dto;
    }

    @Override
    public HotelBooking toEntity(HotelBookingDTO dto) {
        if (dto == null) {
            return null;
        }

        HotelBooking entity = new HotelBooking();
        entity.setId(dto.getId());

        Optional<Hotel> hotelOptional = hotelRepository.findById(dto.getHotelId());
        if (hotelOptional.isPresent()) {
            entity.setHotel(hotelOptional.get());
        } else {
            entity.setHotel(null);
        }

        Optional<HotelRoomType> hotelRoomTypeOptional = hotelRoomTypeRepository.findById(dto.getHotelRoomTypeId());
        if (hotelRoomTypeOptional.isPresent()) {
            entity.setHotelRoomType(hotelRoomTypeOptional.get());
        } else {
            entity.setHotelRoomType(null);
        }

        Optional<Booking> bookingOptional = bookingRepository.findById(dto.getBookingId());
        if (bookingOptional.isPresent()) {
            entity.setBooking(bookingOptional.get());
        } else {
            entity.setBooking(null);
        }

        Optional<User> userOptional = userRepository.findById(dto.getUserId());
        if (userOptional.isPresent()) {
            entity.setUser(userOptional.get());
        } else {
            entity.setUser(null);
        }

        entity.setCheckIn(dto.getCheckIn());
        entity.setCheckOut(dto.getCheckOut());

        mapAuditFieldsToEntity(dto, entity);

        return entity;
    }
}
