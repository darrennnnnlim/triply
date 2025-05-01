package com.example.triply.core.booking.service.template;

import com.example.triply.core.booking.event.BookingConfirmedEvent;
import org.springframework.context.ApplicationEventPublisher;

import com.example.triply.core.booking.dto.BookingDTO;
import com.example.triply.core.booking.dto.HotelBookingAddonDTO;
import com.example.triply.core.booking.dto.HotelBookingDTO;
import com.example.triply.core.booking.dto.HotelBookingResponse;
import com.example.triply.core.booking.entity.Booking;
import com.example.triply.core.booking.entity.BookingStatusEnum;
import com.example.triply.core.booking.entity.hotel.HotelBooking;
import com.example.triply.core.booking.entity.hotel.HotelBookingAddon;
import com.example.triply.core.booking.mapper.BookingMapper;
import com.example.triply.core.booking.mapper.hotel.HotelBookingAddonMapper;
import com.example.triply.core.booking.mapper.hotel.HotelBookingMapper;
import com.example.triply.core.booking.repository.BookingRepository;
import com.example.triply.core.booking.repository.hotel.HotelBookingAddonRepository;
import com.example.triply.core.booking.repository.hotel.HotelBookingRepository;
import com.example.triply.core.hotel.repository.HotelAddonRepository;
import com.example.triply.core.hotel.repository.HotelRepository;
import com.example.triply.core.hotel.repository.HotelRoomPriceRepository;
import com.example.triply.core.hotel.repository.HotelRoomTypeRepository;
import com.example.triply.core.hotel.model.entity.Hotel;
import com.example.triply.core.hotel.model.entity.HotelAddon;
import com.example.triply.core.hotel.model.entity.HotelRoomPrice;
import com.example.triply.core.hotel.model.entity.HotelRoomType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HotelBookingService extends BookingTemplate {
    public static final Logger LOGGER = LoggerFactory.getLogger(HotelBookingService.class);

    private final HotelRepository hotelRepository;
    private final HotelRoomTypeRepository hotelRoomTypeRepository;
    private final HotelAddonRepository hotelAddonRepository;
    private final HotelRoomPriceRepository hotelRoomPriceRepository;
    private final BookingMapper bookingMapper;
    private final HotelBookingMapper hotelBookingMapper;
    private final HotelBookingRepository hotelBookingRepository;
    private final HotelBookingAddonMapper hotelBookingAddonMapper;
    private final HotelBookingAddonRepository hotelBookingAddonRepository;
    private final BookingRepository bookingRepository;
    private final ApplicationEventPublisher eventPublisher;

    public HotelBookingService(HotelRepository hotelRepository,
                             HotelRoomTypeRepository hotelRoomTypeRepository,
                             HotelAddonRepository hotelAddonRepository,
                             HotelRoomPriceRepository hotelRoomPriceRepository,
                             BookingMapper bookingMapper,
                             HotelBookingMapper hotelBookingMapper,
                             HotelBookingRepository hotelBookingRepository,
                             HotelBookingAddonMapper hotelBookingAddonMapper,
                             HotelBookingAddonRepository hotelBookingAddonRepository,
                             BookingRepository bookingRepository,
                             ApplicationEventPublisher eventPublisher) {
        this.hotelRepository = hotelRepository;
        this.hotelRoomTypeRepository = hotelRoomTypeRepository;
        this.hotelAddonRepository = hotelAddonRepository;
        this.hotelRoomPriceRepository = hotelRoomPriceRepository;
        this.bookingMapper = bookingMapper;
        this.bookingRepository = bookingRepository;
        this.hotelBookingMapper = hotelBookingMapper;
        this.hotelBookingRepository = hotelBookingRepository;
        this.hotelBookingAddonMapper = hotelBookingAddonMapper;
        this.hotelBookingAddonRepository = hotelBookingAddonRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected void validateBooking(BookingDTO request) {
        if (request == null || request.getHotelBooking() == null) {
            LOGGER.error("Invalid request: Hotel Booking details are required");
            throw new IllegalArgumentException("Invalid request: Hotel Booking details are required");
        }

        HotelBookingDTO hotelBookingDTO = request.getHotelBooking();

        if (hotelBookingDTO.getHotelId() == null || hotelBookingDTO.getHotelRoomTypeId() == null) {
            LOGGER.error("Hotel ID and Hotel Room Type ID are required");
            throw new IllegalArgumentException("Hotel ID and Hotel Room Type ID are required");
        }

        if (hotelBookingDTO.getCheckIn().isBefore(LocalDateTime.now()) || hotelBookingDTO.getCheckOut().isBefore(LocalDateTime.now())) {
            LOGGER.error("Check In and Check Out dates cannot be in the past");
            throw new IllegalArgumentException("Check In and Check Out dates cannot be in the past");
        }

        Optional<Hotel> hotelOptional = hotelRepository.findById(hotelBookingDTO.getHotelId());
        if (hotelOptional.isEmpty()) {
            LOGGER.error("Hotel ID is not a valid existing hotel data");
            throw new IllegalArgumentException("Hotel ID is not a valid existing hotel data");
        }

        Optional<HotelRoomType> hotelRoomTypeOptional = hotelRoomTypeRepository.findById(hotelBookingDTO.getHotelRoomTypeId());
        if (hotelRoomTypeOptional.isEmpty()) {
            LOGGER.error("Hotel Room Type ID is not a valid existing hotel room type data");
            throw new IllegalArgumentException("Hotel Room Type ID is not a valid existing hotel room type data");
        }
    }

    @Override
    protected BigDecimal calculateAddonPrice(BookingDTO request) {
        if (request.getHotelBookingAddon() == null || request.getHotelBookingAddon().isEmpty()) {
            return BigDecimal.ZERO;
        }

        List<Long> hotelAddonIds = request.getHotelBookingAddon().stream().map(HotelBookingAddonDTO::getHotelAddonId).toList();
        Map<Long, BigDecimal> hotelAddonPriceMap = hotelAddonRepository.findByIdIn(hotelAddonIds).stream().collect(Collectors.toMap(HotelAddon::getId, HotelAddon::getPrice));

        return request.getHotelBookingAddon().stream().map(hotelBookingAddon -> {
                    BigDecimal addonPrice = hotelAddonPriceMap.getOrDefault(hotelBookingAddon.getHotelAddonId(), BigDecimal.ZERO);
                    BigDecimal calculatedAddonPrice = addonPrice.multiply(BigDecimal.valueOf(hotelBookingAddon.getQuantity()));
                    hotelBookingAddon.setTotalPrice(calculatedAddonPrice);
                    return calculatedAddonPrice;
                }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    protected void calculateTotalPrice(BookingDTO request, BigDecimal addOnPrice) {
        BigDecimal totalAdditionalPrice = BigDecimal.ZERO;

        HotelBookingDTO hotelBookingDTO = request.getHotelBooking();
        Optional<HotelRoomType> hotelRoomTypeOptional = hotelRoomTypeRepository.findById(request.getHotelBooking().getHotelRoomTypeId());
        if (hotelRoomTypeOptional.isPresent()) {
            HotelRoomType hotelRoomType = hotelRoomTypeOptional.get();
            BigDecimal basePrice = hotelRoomType.getBasePrice();

            LocalDateTime checkIn = hotelBookingDTO.getCheckIn();
            LocalDateTime checkOut = hotelBookingDTO.getCheckOut();

            Long noOfNights = ChronoUnit.DAYS.between(checkIn.toLocalDate(), checkOut.toLocalDate());
            BigDecimal totalBasePrice = basePrice.multiply(BigDecimal.valueOf(noOfNights));

            List<HotelRoomPrice> hotelRoomPriceList = hotelRoomPriceRepository.findOverlappingPrices(hotelBookingDTO.getHotelRoomTypeId(), checkIn, checkOut);
            for (HotelRoomPrice hotelRoomPrice : hotelRoomPriceList) {
                LocalDate hotelRoomPriceStart = hotelRoomPrice.getStartDate().toLocalDate();
                LocalDate hotelRoomPriceEnd = hotelRoomPrice.getEndDate().toLocalDate();
                LocalDate checkInDate = checkIn.toLocalDate();
                LocalDate checkOutDate = checkOut.toLocalDate();

                LocalDate overlapStart = checkInDate.isAfter(hotelRoomPriceStart) ? checkInDate : hotelRoomPriceStart;
                LocalDate overlapEnd = checkOutDate.isBefore(hotelRoomPriceEnd) ? checkOutDate : hotelRoomPriceEnd;

                Long overlappingNights = ChronoUnit.DAYS.between(overlapStart, overlapEnd);
                totalAdditionalPrice = totalAdditionalPrice.add(hotelRoomPrice.getPrice().multiply(BigDecimal.valueOf(overlappingNights))).add(addOnPrice);
            }

            BigDecimal finalPrice = totalBasePrice.add(totalAdditionalPrice);
            if (request.getFinalPrice() == null) {
                request.setFinalPrice(finalPrice);
            } else {
                request.setFinalPrice(request.getFinalPrice().add(finalPrice));
            }
        }
    }

    @Override
    protected Booking createBooking(BookingDTO request) {
        Booking booking = bookingMapper.toEntity(request);

        booking.setStatus(BookingStatusEnum.PENDING.name());
        booking.setBookingTime(LocalDateTime.now());
        Booking saveBooking = bookingRepository.save(booking);

        request.setId(saveBooking.getId());

        request.getHotelBooking().setBookingId(saveBooking.getId());
        HotelBooking hotelBooking = hotelBookingMapper.toEntity(request.getHotelBooking());
        HotelBooking saveHotelBooking = hotelBookingRepository.save(hotelBooking);

        if (request.getHotelBookingAddon() != null && !request.getHotelBookingAddon().isEmpty()) {
            request.getHotelBookingAddon().forEach(hotelBookingAddonDTO -> hotelBookingAddonDTO.setHotelBookingId(saveHotelBooking.getId()));
            List<HotelBookingAddon> hotelBookingAddonList = hotelBookingAddonMapper.toEntity(request.getHotelBookingAddon());
            hotelBookingAddonRepository.saveAll(hotelBookingAddonList);
        }

        return saveBooking;
    }

    @Override
    protected void confirmBooking(Booking booking) {
        if (booking != null && booking.getId() != null && booking.getStatus().equals(BookingStatusEnum.PENDING.name())) {
            LOGGER.info("Publishing BookingConfirmedEvent for booking ID: {}", booking.getId());
            BookingConfirmedEvent event = new BookingConfirmedEvent(booking);
            eventPublisher.publishEvent(event);
        } else {
            LOGGER.warn("Skipping event publication for booking ID: {} due to null data or non-PENDING status.",
                booking != null ? booking.getId() : "null");
        }
    }

    public List<HotelBookingResponse> getBookingByUserId (Long userId){
        List<HotelBooking> hotelBookings = hotelBookingRepository.findByUserId(userId);
        List<HotelBookingResponse> hotelBookingResponses = new ArrayList<>();
        for (HotelBooking booking : hotelBookings) {
            HotelBookingResponse resp = new HotelBookingResponse();
            resp.setUserId(userId);
            resp.setHotelId(booking.getHotel().getId());
            resp.setCheckIn(booking.getCheckIn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            resp.setCheckOut(booking.getCheckOut().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            resp.setRoomType(booking.getHotelRoomType().getName());
            hotelBookingResponses.add(resp);
        }
        return hotelBookingResponses;
    }

    public List<HotelBookingResponse> getBookingByBookingId (Long bookingId, Long userId){
        List<HotelBooking> hotelBookings = hotelBookingRepository.findByBookingId(bookingId);
        List<HotelBookingResponse> hotelBookingResponses = new ArrayList<>();
        for (HotelBooking booking : hotelBookings) {
            HotelBookingResponse resp = new HotelBookingResponse();
            resp.setUserId(userId);
            resp.setHotelId(booking.getHotel().getId());
            resp.setCheckIn(booking.getCheckIn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            resp.setCheckOut(booking.getCheckOut().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            resp.setRoomType(booking.getHotelRoomType().getName());
            hotelBookingResponses.add(resp);
        }
        return hotelBookingResponses;
    }
}
