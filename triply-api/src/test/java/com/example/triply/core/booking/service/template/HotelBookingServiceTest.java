package com.example.triply.core.booking.service.template;

import com.example.triply.core.booking.dto.BookingDTO;
import com.example.triply.core.booking.dto.HotelBookingAddonDTO;
import com.example.triply.core.booking.dto.HotelBookingDTO;
import com.example.triply.core.booking.dto.HotelBookingResponse;
import com.example.triply.core.booking.entity.Booking;
import com.example.triply.core.booking.entity.hotel.HotelBooking;
import com.example.triply.core.booking.mapper.BookingMapper;
import com.example.triply.core.booking.mapper.hotel.HotelBookingAddonMapper;
import com.example.triply.core.booking.mapper.hotel.HotelBookingMapper;
import com.example.triply.core.booking.repository.BookingRepository;
import com.example.triply.core.booking.repository.hotel.HotelBookingAddonRepository;
import com.example.triply.core.booking.repository.hotel.HotelBookingRepository;
import com.example.triply.core.hotel.mapper.HotelAddonMapper;
import com.example.triply.core.hotel.model.entity.Hotel;
import com.example.triply.core.hotel.model.entity.HotelAddon;
import com.example.triply.core.hotel.model.entity.HotelRoomPrice;
import com.example.triply.core.hotel.model.entity.HotelRoomType;
import com.example.triply.core.hotel.repository.HotelAddonRepository;
import com.example.triply.core.hotel.repository.HotelRepository;
import com.example.triply.core.hotel.repository.HotelRoomPriceRepository;
import com.example.triply.core.hotel.repository.HotelRoomTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HotelBookingServiceTest {

    private HotelBookingService hotelBookingService;

    private HotelRepository hotelRepository;
    private HotelRoomTypeRepository hotelRoomTypeRepository;
    private HotelAddonRepository hotelAddonRepository;
    private HotelRoomPriceRepository hotelRoomPriceRepository;
    private BookingMapper bookingMapper;
    private HotelBookingMapper hotelBookingMapper;
    private HotelBookingRepository hotelBookingRepository;
    private HotelBookingAddonMapper hotelBookingAddonMapper;
    private HotelBookingAddonRepository hotelBookingAddonRepository;
    private BookingRepository bookingRepository;
    private ApplicationEventPublisher eventPublisher;
    private HotelAddonMapper hotelAddonMapper;

    @BeforeEach
    void setUp() {
        hotelRepository = mock(HotelRepository.class);
        hotelRoomTypeRepository = mock(HotelRoomTypeRepository.class);
        hotelAddonRepository = mock(HotelAddonRepository.class);
        hotelRoomPriceRepository = mock(HotelRoomPriceRepository.class);
        bookingMapper = mock(BookingMapper.class);
        hotelBookingMapper = mock(HotelBookingMapper.class);
        hotelBookingRepository = mock(HotelBookingRepository.class);
        hotelBookingAddonMapper = mock(HotelBookingAddonMapper.class);
        hotelBookingAddonRepository = mock(HotelBookingAddonRepository.class);
        bookingRepository = mock(BookingRepository.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        hotelAddonMapper = mock(HotelAddonMapper.class);

        hotelBookingService = new HotelBookingService(hotelRepository, hotelRoomTypeRepository, hotelAddonRepository,
                hotelRoomPriceRepository, bookingMapper, hotelBookingMapper,
                hotelBookingRepository, hotelBookingAddonMapper,
                hotelBookingAddonRepository, bookingRepository,
                eventPublisher, hotelAddonMapper);
    }

    @Test
    void validateBooking_validRequest_doesNotThrow() {
        HotelBookingDTO dto = new HotelBookingDTO();
        dto.setHotelId(1L);
        dto.setHotelRoomTypeId(2L);
        dto.setCheckIn(LocalDateTime.now().plusDays(1));
        dto.setCheckOut(LocalDateTime.now().plusDays(2));

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setHotelBooking(dto);

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(new Hotel()));
        when(hotelRoomTypeRepository.findById(2L)).thenReturn(Optional.of(new HotelRoomType()));

        assertDoesNotThrow(() -> hotelBookingService.validateBooking(bookingDTO));
    }

    @Test
    void calculateAddonPrice_withAddons_returnsTotal() {
        HotelAddon addon = new HotelAddon();
        addon.setId(1L);
        addon.setPrice(BigDecimal.TEN);

        HotelBookingAddonDTO dto = new HotelBookingAddonDTO();
        dto.setHotelAddonId(1L);
        dto.setQuantity(2);

        BookingDTO request = new BookingDTO();
        request.setHotelBookingAddon(List.of(dto));

        when(hotelAddonRepository.findByIdIn(List.of(1L))).thenReturn(List.of(addon));

        BigDecimal result = hotelBookingService.calculateAddonPrice(request);
        assertEquals(new BigDecimal("20"), result);
    }

    @Test
    void calculateAddonPrice_noAddons_returnsZero() {
        BookingDTO request = new BookingDTO();
        request.setHotelBookingAddon(Collections.emptyList());
        assertEquals(BigDecimal.ZERO, hotelBookingService.calculateAddonPrice(request));
    }

    @Test
    void getBookingByUserId_returnsResponses() {
        Hotel hotel = new Hotel(); hotel.setId(10L);
        HotelRoomType roomType = new HotelRoomType(); roomType.setName("Suite");

        HotelBooking hotelBooking = mock(HotelBooking.class);
        when(hotelBooking.getHotel()).thenReturn(hotel);
        when(hotelBooking.getHotelRoomType()).thenReturn(roomType);
        when(hotelBooking.getCheckIn()).thenReturn(LocalDateTime.of(2025, 3, 1, 14, 0));
        when(hotelBooking.getCheckOut()).thenReturn(LocalDateTime.of(2025, 3, 3, 11, 0));

        when(hotelBookingRepository.findByUserId(5L)).thenReturn(List.of(hotelBooking));

        List<HotelBookingResponse> responses = hotelBookingService.getBookingByUserId(5L);

        assertEquals(1, responses.size());
        assertEquals(5L, responses.getFirst().getUserId());
        assertEquals(10L, responses.getFirst().getHotelId());
        assertEquals("Suite", responses.getFirst().getRoomType());
    }

    @Test
    void getBookingByBookingId_returnsResponses() {
        Hotel hotel = new Hotel(); hotel.setId(20L);
        HotelRoomType roomType = new HotelRoomType(); roomType.setName("Deluxe");

        HotelBooking hotelBooking = mock(HotelBooking.class);
        when(hotelBooking.getHotel()).thenReturn(hotel);
        when(hotelBooking.getHotelRoomType()).thenReturn(roomType);
        when(hotelBooking.getCheckIn()).thenReturn(LocalDateTime.of(2025, 6, 10, 15, 0));
        when(hotelBooking.getCheckOut()).thenReturn(LocalDateTime.of(2025, 6, 12, 12, 0));

        when(hotelBookingRepository.findByBookingId(20L)).thenReturn(List.of(hotelBooking));

        List<HotelBookingResponse> responses = hotelBookingService.getBookingByBookingId(20L, 9L);

        assertEquals(1, responses.size());
        assertEquals(9L, responses.getFirst().getUserId());
        assertEquals(20L, responses.getFirst().getHotelId());
        assertEquals("Deluxe", responses.getFirst().getRoomType());
    }

    @Test
    void validateBooking_missingHotelBooking_throwsException() {
        BookingDTO bookingDTO = new BookingDTO();
        Exception ex = assertThrows(IllegalArgumentException.class, () -> hotelBookingService.validateBooking(bookingDTO));
        assertEquals("Invalid request: Hotel Booking details are required", ex.getMessage());
    }

    @Test
    void validateBooking_missingIds_throwsException() {
        HotelBookingDTO dto = new HotelBookingDTO();
        dto.setCheckIn(LocalDateTime.now().plusDays(1));
        dto.setCheckOut(LocalDateTime.now().plusDays(2));

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setHotelBooking(dto);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> hotelBookingService.validateBooking(bookingDTO));
        assertEquals("Hotel ID and Hotel Room Type ID are required", ex.getMessage());
    }

    @Test
    void validateBooking_pastDates_throwsException() {
        HotelBookingDTO dto = new HotelBookingDTO();
        dto.setHotelId(1L);
        dto.setHotelRoomTypeId(2L);
        dto.setCheckIn(LocalDateTime.now().minusDays(1));
        dto.setCheckOut(LocalDateTime.now().minusDays(2));

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setHotelBooking(dto);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> hotelBookingService.validateBooking(bookingDTO));
        assertEquals("Check In and Check Out dates cannot be in the past", ex.getMessage());
    }

    @Test
    void validateBooking_invalidHotelId_throwsException() {
        HotelBookingDTO dto = new HotelBookingDTO();
        dto.setHotelId(999L);
        dto.setHotelRoomTypeId(1L);
        dto.setCheckIn(LocalDateTime.now().plusDays(1));
        dto.setCheckOut(LocalDateTime.now().plusDays(2));

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setHotelBooking(dto);

        when(hotelRepository.findById(999L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> hotelBookingService.validateBooking(bookingDTO));
        assertEquals("Hotel ID is not a valid existing hotel data", ex.getMessage());
    }

    @Test
    void validateBooking_invalidRoomTypeId_throwsException() {
        HotelBookingDTO dto = new HotelBookingDTO();
        dto.setHotelId(1L);
        dto.setHotelRoomTypeId(999L);
        dto.setCheckIn(LocalDateTime.now().plusDays(1));
        dto.setCheckOut(LocalDateTime.now().plusDays(2));

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setHotelBooking(dto);

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(new Hotel()));
        when(hotelRoomTypeRepository.findById(999L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(IllegalArgumentException.class, () -> hotelBookingService.validateBooking(bookingDTO));
        assertEquals("Hotel Room Type ID is not a valid existing hotel room type data", ex.getMessage());
    }

    @Test
    void calculateTotalPrice_appliesBasePriceWithNoOverlap() {
        HotelRoomType roomType = new HotelRoomType();
        roomType.setBasePrice(BigDecimal.valueOf(100));

        HotelBookingDTO dto = new HotelBookingDTO();
        dto.setHotelRoomTypeId(1L);
        dto.setCheckIn(LocalDateTime.of(2025, 1, 1, 14, 0));
        dto.setCheckOut(LocalDateTime.of(2025, 1, 4, 12, 0));

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setHotelBooking(dto);

        when(hotelRoomTypeRepository.findById(1L)).thenReturn(Optional.of(roomType));
        when(hotelRoomPriceRepository.findOverlappingPrices(any(), any(), any())).thenReturn(Collections.emptyList());

        hotelBookingService.calculateTotalPrice(bookingDTO, BigDecimal.ZERO);

        assertEquals(BigDecimal.valueOf(300), bookingDTO.getFinalPrice());
    }

    @Test
    void createBooking_createsAndSavesAll() {
        BookingDTO bookingDTO = new BookingDTO();
        HotelBookingDTO hotelBookingDTO = new HotelBookingDTO();
        bookingDTO.setHotelBooking(hotelBookingDTO);

        Booking bookingEntity = new Booking();
        bookingEntity.setId(1L);

        HotelBooking hotelBookingEntity = new HotelBooking();

        when(bookingMapper.toEntity(bookingDTO)).thenReturn(bookingEntity);
        when(bookingRepository.save(any())).thenReturn(bookingEntity);
        when(hotelBookingMapper.toEntity(hotelBookingDTO)).thenReturn(hotelBookingEntity);
        when(hotelBookingRepository.save(any())).thenReturn(hotelBookingEntity);

        Booking result = hotelBookingService.createBooking(bookingDTO);

        assertEquals(1L, result.getId());
    }

    @Test
    void calculateTotalPrice_withOverlappingRoomPrices_addsToTotal() {
        HotelRoomType roomType = new HotelRoomType();
        roomType.setBasePrice(BigDecimal.valueOf(100));

        HotelBookingDTO dto = new HotelBookingDTO();
        dto.setHotelRoomTypeId(1L);
        dto.setCheckIn(LocalDateTime.of(2025, 5, 1, 14, 0));
        dto.setCheckOut(LocalDateTime.of(2025, 5, 4, 12, 0));

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setHotelBooking(dto);

        HotelRoomPrice overlappingPrice = new HotelRoomPrice();
        overlappingPrice.setStartDate(LocalDateTime.of(2025, 5, 2, 0, 0));
        overlappingPrice.setEndDate(LocalDateTime.of(2025, 5, 3, 0, 0));
        overlappingPrice.setPrice(BigDecimal.valueOf(50));

        when(hotelRoomTypeRepository.findById(1L)).thenReturn(Optional.of(roomType));
        when(hotelRoomPriceRepository.findOverlappingPrices(eq(1L), any(), any())).thenReturn(List.of(overlappingPrice));

        hotelBookingService.calculateTotalPrice(bookingDTO, BigDecimal.ZERO);

        // base price: 3 nights * 100 = 300
        // overlap: 1 night * 50 = 50
        assertEquals(BigDecimal.valueOf(350), bookingDTO.getFinalPrice());
    }
}