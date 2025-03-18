package com.example.triply.core.booking.service.template;

import com.example.triply.core.booking.dto.BookingDTO;
import com.example.triply.core.booking.dto.FlightBookingResponse;
import com.example.triply.core.booking.dto.flight.FlightBookingAddonDTO;
import com.example.triply.core.booking.dto.flight.FlightBookingDTO;
import com.example.triply.core.booking.entity.Booking;
import com.example.triply.core.booking.entity.BookingStatusEnum;
import com.example.triply.core.booking.entity.flight.*;
import com.example.triply.core.booking.mapper.BookingMapper;
import com.example.triply.core.booking.mapper.flight.FlightBookingAddonMapper;
import com.example.triply.core.booking.mapper.flight.FlightBookingMapper;
import com.example.triply.core.booking.repository.*;
import com.example.triply.core.booking.repository.flight.*;
import com.example.triply.core.booking.repository.flight.FlightBookingRepository;
import com.example.triply.core.booking.repository.flight.FlightRepository;
import com.example.triply.core.booking.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FlightBookingService extends BookingTemplate {
    public static final Logger LOGGER = LoggerFactory.getLogger(FlightBookingService.class);

    private final FlightRepository flightRepository;

    private final FlightClassRepository flightClassRepository;

    private final FlightAddonPriceRepository flightAddonPriceRepository;

    private final FlightPriceRepository flightPriceRepository;

    private final BookingMapper bookingMapper;

    private final FlightBookingMapper flightBookingMapper;

    private final FlightBookingRepository flightBookingRepository;

    private final FlightBookingAddonRepository flightBookingAddonRepository;

    private final FlightBookingAddonMapper flightBookingAddonMapper;

    private final BookingRepository bookingRepository;

    public FlightBookingService(FlightRepository flightRepository, FlightClassRepository flightClassRepository, FlightAddonPriceRepository flightAddonPriceRepository, FlightPriceRepository flightPriceRepository, BookingMapper bookingMapper, FlightBookingMapper flightBookingMapper, FlightBookingRepository flightBookingRepository, FlightBookingAddonRepository flightBookingAddonRepository, FlightBookingAddonMapper flightBookingAddonMapper, BookingRepository bookingRepository) {
        this.flightRepository = flightRepository;
        this.flightClassRepository = flightClassRepository;
        this.flightAddonPriceRepository = flightAddonPriceRepository;
        this.flightPriceRepository = flightPriceRepository;
        this.bookingMapper = bookingMapper;
        this.flightBookingMapper = flightBookingMapper;
        this.flightBookingRepository = flightBookingRepository;
        this.flightBookingAddonRepository = flightBookingAddonRepository;
        this.flightBookingAddonMapper = flightBookingAddonMapper;
        this.bookingRepository = bookingRepository;
    }

    @Override
    protected void validateBooking(BookingDTO request) {
        if (request == null || request.getFlightBooking() == null) {
            LOGGER.error("Invalid request: Flight Booking details are required");
            throw new IllegalArgumentException("Invalid request: Flight Booking details are required");
        }

        FlightBookingDTO flightBookingDTO = request.getFlightBooking();

        if (flightBookingDTO.getFlightId() == null || flightBookingDTO.getFlightClassId() == null) {
            LOGGER.error("Flight ID and Flight Class ID are required");
            throw new IllegalArgumentException("Flight ID and Flight Class ID are required");
        }

        if (flightBookingDTO.getDepartureDate().isBefore(LocalDateTime.now())) {
            LOGGER.error("Departure Date cannot be in the past");
            throw new IllegalArgumentException("Departure Date cannot be in the past");
        }

        Optional<Flight> flightOptional = flightRepository.findById(flightBookingDTO.getFlightId());
        if (flightOptional.isEmpty()) {
            LOGGER.error("Flight ID is not a valid existing flight data");
            throw new IllegalArgumentException("Flight ID is not a valid existing flight data");
        }

        Optional<FlightClass> flightClassOptional = flightClassRepository.findById(flightBookingDTO.getFlightClassId());
        if (flightClassOptional.isEmpty()) {
            LOGGER.error("Flight Class ID is not a valid existing flight class data");
            throw new IllegalArgumentException("Flight Class ID is not a valid existing flight class data");
        }
    }

    @Override
    protected BigDecimal calculateAddonPrice(BookingDTO request) {
        if (request.getFlightBookingAddon() == null || request.getFlightBookingAddon().isEmpty()) {
            return BigDecimal.ZERO;
        }

        List<Long> flightAddonIds = request.getFlightBookingAddon().stream().map(FlightBookingAddonDTO::getFlightAddonId).toList();
        Map<Long, BigDecimal> flightAddonPriceMap = flightAddonPriceRepository.findByFlightAndFlightAddonIn(request.getFlightBooking().getFlightId(), flightAddonIds).stream().collect(Collectors.toMap(flightAddonPrice -> flightAddonPrice.getFlightAddon().getId(), FlightAddonPrice::getPrice));

        return request.getFlightBookingAddon().stream().map(flightBookingAddon -> {
           BigDecimal addonPrice = flightAddonPriceMap.getOrDefault(flightBookingAddon.getFlightAddonId(), BigDecimal.ZERO);
           BigDecimal calculatedAddonPrice = addonPrice.multiply(BigDecimal.valueOf(flightBookingAddon.getQuantity()));
           flightBookingAddon.setPrice(calculatedAddonPrice);
           return calculatedAddonPrice;
        }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    protected void calculateTotalPrice(BookingDTO request, BigDecimal addOnPrice) {
        FlightBookingDTO flightBookingDTO = request.getFlightBooking();
        Optional<FlightPrice> flightPriceOptional = flightPriceRepository.findByFlightAndFlightClass(flightBookingDTO.getFlightId(), flightBookingDTO.getFlightClassId());

        if (flightPriceOptional.isPresent()) {
            FlightPrice flightPrice = flightPriceOptional.get();
            BigDecimal basePrice = flightPrice.getBasePrice();

            BigDecimal discountFactor = BigDecimal.ONE.subtract(flightPrice.getDiscount().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
            BigDecimal discountedPrice = basePrice.multiply(discountFactor);
            BigDecimal surgeMultiplierPrice = discountedPrice.multiply(flightPrice.getSurgeMultiplier());
            BigDecimal finalPrice = surgeMultiplierPrice.add(addOnPrice);
            if (request.getFinalPrice() == null) {
                request.setFinalPrice(finalPrice);
            } else {
                request.setFinalPrice(request.getFinalPrice().add(finalPrice));
            }
        } else {
            LOGGER.error("Flight ID and Flight Class ID combination not found in Flight Price");
            throw new IllegalArgumentException("Flight ID and Flight Class ID combination not found in Flight Price");
        }
    }

    @Override
    protected Booking createBooking(BookingDTO request) {
        Booking booking = bookingMapper.toEntity(request);

        booking.setStatus(BookingStatusEnum.PENDING.name());
        booking.setBookingTime(LocalDateTime.now());
        Booking saveBooking = bookingRepository.save(booking);
        request.setId(saveBooking.getId());
        request.setStatus(saveBooking.getStatus());
        request.setBookingTime(saveBooking.getBookingTime());

        request.getFlightBooking().setBookingId(saveBooking.getId());
        FlightBooking flightBooking = flightBookingMapper.toEntity(request.getFlightBooking());
        FlightBooking saveFlightBooking = flightBookingRepository.save(flightBooking);
        request.setFlightBooking(flightBookingMapper.toDto(saveFlightBooking));

        if (request.getFlightBookingAddon() != null && !request.getFlightBookingAddon().isEmpty()) {
            request.getFlightBookingAddon().forEach(flightBookingAddonDTO -> flightBookingAddonDTO.setFlightBookingId(saveFlightBooking.getId()));
            List<FlightBookingAddon> flightBookingAddonList = flightBookingAddonMapper.toEntity(request.getFlightBookingAddon());
            flightBookingAddonRepository.saveAll(flightBookingAddonList);
        }

        return saveBooking;
    }

    @Override
    protected void confirmBooking(Booking booking) {

    }

    public List<FlightBookingResponse> getBookingByUserId (Long userId){
        List<FlightBooking> flightBooking = flightBookingRepository.findByUserId(userId);
        List<FlightBookingResponse> flightBookingResponse = new ArrayList<>();
        for (FlightBooking booking : flightBooking) {
            FlightBookingResponse resp = new FlightBookingResponse();
            resp.setUserId(userId);
            resp.setBookingId(booking.getBooking().getId());
            resp.setDepartureDate(booking.getDepartureDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            resp.setFlightId(booking.getFlight().getId());
            flightBookingResponse.add(resp);
        }
        return flightBookingResponse;
    }

    public List<FlightBookingResponse> getBookingByBookingId (Long bookingId, Long userId){
        List<FlightBooking> flightBooking = flightBookingRepository.findByBookingId(bookingId);
        List<FlightBookingResponse> flightBookingResponse = new ArrayList<>();
        for (FlightBooking booking : flightBooking) {
            FlightBookingResponse resp = new FlightBookingResponse();
            resp.setUserId(userId);
            resp.setDepartureDate(booking.getDepartureDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            resp.setFlightId(booking.getFlight().getId());
            flightBookingResponse.add(resp);
        }
        return flightBookingResponse;
    }
}
