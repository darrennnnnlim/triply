package com.example.triply.core.booking.service.impl;

import com.example.triply.core.booking.dto.BookingDTO;
import com.example.triply.core.booking.entity.Booking;
import com.example.triply.core.booking.entity.BookingStatusEnum;
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
import com.example.triply.core.flight.mapper.AirlineMapper;
import com.example.triply.core.flight.mapper.FlightAddonMapper;
import com.example.triply.core.flight.mapper.FlightMapper;
import com.example.triply.core.flight.model.entity.Airline;
import com.example.triply.core.flight.model.entity.Flight;
import com.example.triply.core.flight.model.entity.FlightAddon;
import com.example.triply.core.flight.repository.AirlineRepository;
import com.example.triply.core.flight.repository.FlightAddonRepository;
import com.example.triply.core.flight.repository.FlightRepository;
import com.example.triply.core.hotel.mapper.HotelAddonMapper;
import com.example.triply.core.hotel.mapper.HotelMapper;
import com.example.triply.core.hotel.mapper.HotelRoomTypeBasicMapper;
import com.example.triply.core.hotel.model.entity.Hotel;
import com.example.triply.core.hotel.model.entity.HotelAddon;
import com.example.triply.core.hotel.model.entity.HotelRoomType;
import com.example.triply.core.hotel.repository.HotelAddonRepository;
import com.example.triply.core.hotel.repository.HotelRepository;
import com.example.triply.core.hotel.repository.HotelRoomTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private final AirlineRepository airlineRepository;
    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;
    private final AirlineMapper airlineMapper;
    private final HotelMapper hotelMapper;
    private final HotelRoomTypeBasicMapper hotelRoomTypeBasicMapper;
    private final HotelRepository hotelRepository;
    private final HotelRoomTypeRepository hotelRoomTypeRepository;
    private final HotelAddonRepository hotelAddonRepository;
    private final FlightAddonRepository flightAddonRepository;
    private final FlightAddonMapper flightAddonMapper;
    private final HotelAddonMapper hotelAddonMapper;

    public BookingServiceImpl(FlightBookingService flightBookingService, HotelBookingService hotelBookingService, BookingRepository bookingRepository, BookingMapper bookingMapper, FlightBookingRepository flightBookingRepository, FlightBookingMapper flightBookingMapper, HotelBookingMapper hotelBookingMapper, HotelBookingRepository hotelBookingRepository, FlightBookingAddonRepository flightBookingAddonRepository, HotelBookingAddonRepository hotelBookingAddonRepository, FlightBookingAddonMapper flightBookingAddonMapper, HotelBookingAddonMapper hotelBookingAddonMapper, AirlineRepository airlineRepository, FlightRepository flightRepository, FlightMapper flightMapper, AirlineMapper airlineMapper, HotelMapper hotelMapper, HotelRoomTypeBasicMapper hotelRoomTypeBasicMapper, HotelRepository hotelRepository, HotelRoomTypeRepository hotelRoomTypeRepository, HotelAddonRepository hotelAddonRepository, FlightAddonRepository flightAddonRepository, FlightAddonMapper flightAddonMapper, HotelAddonMapper hotelAddonMapper) {
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
        this.airlineRepository = airlineRepository;
        this.flightRepository = flightRepository;
        this.flightMapper = flightMapper;
        this.airlineMapper = airlineMapper;
        this.hotelMapper = hotelMapper;
        this.hotelRoomTypeBasicMapper = hotelRoomTypeBasicMapper;
        this.hotelRepository = hotelRepository;
        this.hotelRoomTypeRepository = hotelRoomTypeRepository;
        this.hotelAddonRepository = hotelAddonRepository;
        this.flightAddonRepository = flightAddonRepository;
        this.flightAddonMapper = flightAddonMapper;
        this.hotelAddonMapper = hotelAddonMapper;
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

        Map<Long, Airline> airlineMap = airlineRepository.findAll().stream().collect(Collectors.toMap(Airline::getId, al -> al));
        Map<Long, Flight> flightMap = flightRepository.findAll().stream().collect(Collectors.toMap(Flight::getId, fl -> fl));
        Map<Long, Hotel> hotelMap = hotelRepository.findAll().stream().collect(Collectors.toMap(Hotel::getId, hl -> hl));
        Map<Long, HotelRoomType> hotelRoomTypeMap = hotelRoomTypeRepository.findAll().stream().collect(Collectors.toMap(HotelRoomType::getId, hrt -> hrt));

        bookingDTOList.forEach(bookingDTO -> {
            bookingDTO.setFlightBooking(flightBookingMapper.toDto(flightBookingMap.get(bookingDTO.getId())));
            if (bookingDTO.getFlightBooking() != null) {
                bookingDTO.getFlightBooking().setFlight(flightMapper.toDto(flightMap.get(bookingDTO.getFlightBooking().getFlightId())));
                bookingDTO.getFlightBooking().getFlight().setAirline(airlineMapper.toDto(airlineMap.get(bookingDTO.getFlightBooking().getFlight().getAirlineId())));
            }

            bookingDTO.setHotelBooking(hotelBookingMapper.toDto(hotelBookingMap.get(bookingDTO.getId())));
            if (bookingDTO.getHotelBooking() != null) {
                bookingDTO.getHotelBooking().setHotel(hotelMapper.toDto(hotelMap.get(bookingDTO.getHotelBooking().getHotelId())));
                bookingDTO.getHotelBooking().setHotelRoomType(hotelRoomTypeBasicMapper.toDto(hotelRoomTypeMap.get(bookingDTO.getHotelBooking().getHotelRoomTypeId())));
            }
        });

        List<Long> flightBookingIds = flightBookingList.stream().map(FlightBooking::getId).toList();
        List<Long> hotelBookingIds = hotelBookingList.stream().map(HotelBooking::getId).toList();

        List<FlightBookingAddon> flightBookingAddonList = flightBookingAddonRepository.findByFlightBookingIdIn(flightBookingIds);
        List<HotelBookingAddon> hotelBookingAddonList = hotelBookingAddonRepository.findByHotelBookingIdIn(hotelBookingIds);

        Map<Long, List<FlightBookingAddon>> flightBookingAddonMap = flightBookingAddonList.stream().collect(Collectors.groupingBy(fba -> fba.getFlightBooking().getId()));
        Map<Long, List<HotelBookingAddon>> hotelBookingAddonMap = hotelBookingAddonList.stream().collect(Collectors.groupingBy(hba -> hba.getHotelBooking().getId()));
        Map<Long, FlightAddon> flightAddonMap = flightAddonRepository.findAll().stream().collect(Collectors.toMap(FlightAddon::getId, fa -> fa));
        Map<Long, HotelAddon> hotelAddonMap = hotelAddonRepository.findAll().stream().collect(Collectors.toMap(HotelAddon::getId, ha -> ha));

        bookingDTOList.forEach(bookingDTO -> {
            if (bookingDTO.getFlightBooking() != null) {
                bookingDTO.setFlightBookingAddon(flightBookingAddonMapper.toDto(flightBookingAddonMap.getOrDefault(bookingDTO.getFlightBooking().getId(), List.of())));
                if (bookingDTO.getFlightBookingAddon() != null && !bookingDTO.getFlightBookingAddon().isEmpty()) {
                    bookingDTO.getFlightBookingAddon().forEach(flightBookingAddonDTO ->
                            flightBookingAddonDTO.setFlightAddon(flightAddonMapper.toDto(flightAddonMap.getOrDefault(flightBookingAddonDTO.getFlightAddonId(), new FlightAddon())))
                    );
                }
            }

            if (bookingDTO.getHotelBooking() != null) {
                bookingDTO.setHotelBookingAddon(hotelBookingAddonMapper.toDto(hotelBookingAddonMap.getOrDefault(bookingDTO.getHotelBooking().getId(), List.of())));
                if (bookingDTO.getHotelBookingAddon() != null && !bookingDTO.getHotelBookingAddon().isEmpty()) {
                    bookingDTO.getHotelBookingAddon().forEach(hotelBookingAddonDTO ->
                            hotelBookingAddonDTO.setHotelAddon(hotelAddonMapper.toDto(hotelAddonMap.getOrDefault(hotelBookingAddonDTO.getHotelAddonId(), new HotelAddon())))
                    );
                }
            }
        });

        return bookingDTOList;
    }

    @Override
    public BookingDTO cancelBooking(Long id) {
        Optional<Booking> bookingOptional = bookingRepository.findById(id);
        if (bookingOptional.isPresent()) {
            Booking booking = bookingOptional.get();
            booking.setStatus(BookingStatusEnum.CANCELLED.name());
            bookingRepository.save(booking);

            return bookingMapper.toDto(booking);
        }

        return null;
    }
}
