package com.example.triply.core.booking.service.impl;

import com.example.triply.core.booking.dto.BookingDTO;
import com.example.triply.core.booking.entity.Booking;
import com.example.triply.core.booking.entity.flight.FlightBooking;
import com.example.triply.core.booking.entity.flight.FlightBookingAddon;
import com.example.triply.core.booking.entity.hotel.HotelBooking;
import com.example.triply.core.booking.entity.hotel.HotelBookingAddon;
import com.example.triply.core.booking.mapper.BookingMapper;
import com.example.triply.core.booking.mapper.flight.FlightBookingAddonMapper;
import com.example.triply.core.booking.mapper.flight.FlightBookingMapper;
import com.example.triply.core.booking.mapper.hotel.HotelBookingAddonMapper;
import com.example.triply.core.booking.mapper.hotel.HotelBookingMapper;
import com.example.triply.core.booking.repository.BookingRepository;
import com.example.triply.core.booking.repository.flight.FlightBookingAddonRepository;
import com.example.triply.core.booking.repository.flight.FlightBookingRepository;
import com.example.triply.core.booking.repository.hotel.HotelBookingAddonRepository;
import com.example.triply.core.booking.repository.hotel.HotelBookingRepository;
import com.example.triply.core.booking.service.BookingService;
import com.example.triply.core.booking.service.template.FlightBookingService;
import com.example.triply.core.booking.service.template.HotelBookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final FlightBookingService flightBookingService;
    private final HotelBookingService hotelBookingService;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final FlightBookingRepository flightBookingRepository;
    private final FlightBookingMapper flightBookingMapper;
    private final HotelBookingMapper hotelBookingMapper;
    private final HotelBookingRepository hotelBookingRepository;
    private final FlightBookingAddonRepository flightBookingAddonRepository;
    private final HotelBookingAddonRepository hotelBookingAddonRepository;
    private final FlightBookingAddonMapper flightBookingAddonMapper;
    private final HotelBookingAddonMapper hotelBookingAddonMapper;

    public BookingServiceImpl(FlightBookingService flightBookingService, HotelBookingService hotelBookingService, BookingRepository bookingRepository, BookingMapper bookingMapper, FlightBookingRepository flightBookingRepository, FlightBookingMapper flightBookingMapper, HotelBookingMapper hotelBookingMapper, HotelBookingRepository hotelBookingRepository, FlightBookingAddonRepository flightBookingAddonRepository, HotelBookingAddonRepository hotelBookingAddonRepository, FlightBookingAddonMapper flightBookingAddonMapper, HotelBookingAddonMapper hotelBookingAddonMapper) {
        this.flightBookingService = flightBookingService;
        this.hotelBookingService = hotelBookingService;
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.flightBookingRepository = flightBookingRepository;
        this.flightBookingMapper = flightBookingMapper;
        this.hotelBookingMapper = hotelBookingMapper;
        this.hotelBookingRepository = hotelBookingRepository;
        this.flightBookingAddonRepository = flightBookingAddonRepository;
        this.hotelBookingAddonRepository = hotelBookingAddonRepository;
        this.flightBookingAddonMapper = flightBookingAddonMapper;
        this.hotelBookingAddonMapper = hotelBookingAddonMapper;
    }

    @Override
    public BookingDTO processBooking(BookingDTO bookingDTO) {
        try {

            // If both Flight Booking and Hotel Booking are null, then why do booking?
            if (bookingDTO.getFlightBooking() == null && bookingDTO.getHotelBooking() == null) {
                LOGGER.error("Invalid request: Flight Booking and Hotel Booking details are required");
                throw new IllegalArgumentException("Invalid request: Flight Booking and Hotel Booking details are required");
            }

            if (bookingDTO.getFlightBooking() != null) {
                flightBookingService.processBooking(bookingDTO);
            }

            if (bookingDTO.getHotelBooking() != null) {
                hotelBookingService.processBooking(bookingDTO);
            }

            return bookingDTO;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Process Booking Bad Request");
        }
    }

    @Override
    public List<BookingDTO> getBooking(String username) {
        List<Booking> bookingList = bookingRepository.findByUser_Username(username);
        List<BookingDTO> bookingDTOList = bookingMapper.toDto(bookingList);

        List<Long> bookingIds = bookingDTOList.stream().map(BookingDTO::getId).toList();

        List<FlightBooking> flightBookingList = flightBookingRepository.findByFlightIdIn(bookingIds);
        List<HotelBooking> hotelBookingList = hotelBookingRepository.findByHotelIdIn(bookingIds);

        Map<Long, FlightBooking> flightBookingMap = flightBookingList.stream().collect(Collectors.toMap(fb -> fb.getBooking().getId(), fb -> fb));
        Map<Long, HotelBooking> hotelBookingMap = hotelBookingList.stream().collect(Collectors.toMap(hb -> hb.getBooking().getId(), hb -> hb));

        bookingDTOList.forEach(bookingDTO -> {
            bookingDTO.setFlightBooking(flightBookingMapper.toDto(flightBookingMap.get(bookingDTO.getId())));
            bookingDTO.setHotelBooking(hotelBookingMapper.toDto(hotelBookingMap.get(bookingDTO.getId())));
        });

        List<Long> flightBookingIds = flightBookingList.stream().map(FlightBooking::getId).toList();
        List<Long> hotelBookingIds = hotelBookingList.stream().map(HotelBooking::getId).toList();

        List<FlightBookingAddon> flightBookingAddonList = flightBookingAddonRepository.findByFlightBookingIdIn(flightBookingIds);
        List<HotelBookingAddon> hotelBookingAddonList = hotelBookingAddonRepository.findByHotelBookingIdIn(hotelBookingIds);

        Map<Long, List<FlightBookingAddon>> flightBookingAddonMap = flightBookingAddonList.stream().collect(Collectors.groupingBy(fba -> fba.getFlightBooking().getId()));
        Map<Long, List<HotelBookingAddon>> hotelBookingAddonMap = hotelBookingAddonList.stream().collect(Collectors.groupingBy(hba -> hba.getHotelBooking().getId()));

        bookingDTOList.forEach(bookingDTO -> {
            if (bookingDTO.getFlightBooking() != null) {
                bookingDTO.setFlightBookingAddon(flightBookingAddonMapper.toDto(flightBookingAddonMap.getOrDefault(bookingDTO.getFlightBooking().getId(), List.of())));
            }

            if (bookingDTO.getHotelBooking() != null) {
                bookingDTO.setHotelBookingAddon(hotelBookingAddonMapper.toDto(hotelBookingAddonMap.getOrDefault(bookingDTO.getHotelBooking().getId(), List.of())));
            }
        });

        return bookingDTOList;
    }
}
