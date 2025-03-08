package com.example.triply.core.booking.mapper;

import com.example.triply.common.mapper.BaseMapper;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.auth.repository.UserRepository;
import com.example.triply.core.booking.dto.BookingDTO;
import com.example.triply.core.booking.entity.Booking;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BookingMapper implements BaseMapper<Booking, BookingDTO> {

    private final UserRepository userRepository;

    public BookingMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public BookingDTO toDto(Booking entity) {
        if (entity == null) {
            return null;
        }

        BookingDTO dto = new BookingDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser().getId());
        dto.setFinalPrice(entity.getFinalPrice());
        dto.setStatus(entity.getStatus());
        dto.setBookingTime(entity.getBookingTime());

        return dto;
    }

    @Override
    public Booking toEntity(BookingDTO dto) {
        if (dto == null) {
            return null;
        }

        Booking entity = new Booking();
        entity.setId(dto.getId());

        Optional<User> user = userRepository.findById(dto.getUserId());
        if (user.isPresent()) {
            entity.setUser(user.get());
        } else {
            entity.setUser(null);
        }

        entity.setFinalPrice(dto.getFinalPrice());
        entity.setStatus(dto.getStatus());
        entity.setBookingTime(dto.getBookingTime());

        return entity;
    }

    @Override
    public List<BookingDTO> toDto(List<Booking> entities) {
        return null;
    }

    @Override
    public List<Booking> toEntity(List<BookingDTO> dto) {
        return null;
    }
}
