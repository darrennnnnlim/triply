package com.example.triply.core.booking.mapper.flight;

import com.example.triply.common.mapper.BaseMapper;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.auth.repository.UserRepository;
import com.example.triply.core.booking.dto.flight.FlightBookingDTO;
import com.example.triply.core.booking.entity.Booking;
import com.example.triply.core.booking.entity.flight.Flight;
import com.example.triply.core.booking.entity.flight.FlightBooking;
import com.example.triply.core.booking.entity.flight.FlightClass;
import com.example.triply.core.booking.repository.BookingRepository;
import com.example.triply.core.booking.repository.flight.FlightClassRepository;
import com.example.triply.core.booking.repository.flight.FlightRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FlightBookingMapper implements BaseMapper<FlightBooking, FlightBookingDTO> {

    private final FlightRepository flightRepository;

    private final FlightClassRepository flightClassRepository;

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;
    private final FlightMapper flightMapper;

    public FlightBookingMapper(FlightRepository flightRepository, FlightClassRepository flightClassRepository, BookingRepository bookingRepository, UserRepository userRepository, FlightMapper flightMapper) {
        this.flightRepository = flightRepository;
        this.flightClassRepository = flightClassRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.flightMapper = flightMapper;
    }

    @Override
    public FlightBookingDTO toDto(FlightBooking entity) {
        if (entity == null) {
            return null;
        }

        FlightBookingDTO dto = new FlightBookingDTO();
        dto.setId(entity.getId());
        dto.setFlightId(entity.getFlight().getId());
        dto.setFlightClassId(entity.getFlightClass().getId());
        dto.setBookingId(entity.getBooking().getId());
        dto.setUserId(entity.getUser().getId());
        dto.setDepartureDate(entity.getDepartureDate());

        Optional<Flight> flightOptional = flightRepository.findById(dto.getFlightId());
        if (flightOptional.isPresent()) {
            dto.setFlight(flightMapper.toDto(flightOptional.get()));
        } else {
            dto.setFlight(null);
        }

        mapAuditFieldsToDto(entity, dto);

        return dto;
    }

    @Override
    public FlightBooking toEntity(FlightBookingDTO dto) {
        if (dto == null) {
            return null;
        }

        FlightBooking entity = new FlightBooking();
        entity.setId(dto.getId());

        Optional<Flight> flightOptional = flightRepository.findById(dto.getFlightId());
        if (flightOptional.isPresent()) {
            entity.setFlight(flightOptional.get());
        } else {
            entity.setFlight(null);
        }

        Optional<FlightClass> flightClassOptional  = flightClassRepository.findById(dto.getFlightClassId());
        if (flightClassOptional.isPresent()) {
            entity.setFlightClass(flightClassOptional.get());
        } else {
            entity.setFlight(null);
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

        entity.setDepartureDate(dto.getDepartureDate());

        mapAuditFieldsToEntity(dto, entity);

        return entity;

    }
}
