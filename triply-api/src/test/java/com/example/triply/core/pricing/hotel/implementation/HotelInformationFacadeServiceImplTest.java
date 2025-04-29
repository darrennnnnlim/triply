package com.example.triply.core.pricing.hotel.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.triply.core.hotel.model.entity.Hotel;
import com.example.triply.core.hotel.model.entity.HotelRoomPrice;
import com.example.triply.core.hotel.model.entity.HotelRoomType;
import com.example.triply.core.hotel.repository.HotelRepository;
import com.example.triply.core.hotel.repository.HotelRoomPriceRepository;
import com.example.triply.core.hotel.repository.HotelRoomTypeRepository;
import com.example.triply.core.pricing.hotel.model.dto.HotelOfferDTO;
import com.example.triply.core.search.hotel.model.dto.HotelSearchRequestDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class HotelInformationFacadeServiceImplTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private HotelRoomTypeRepository hotelRoomTypeRepository;

    @Mock
    private HotelRoomPriceRepository hotelRoomPriceRepository;

    @InjectMocks
    private HotelInformationFacadeServiceImpl hotelInformationService;

    private HotelSearchRequestDTO requestDTO;
    private Hotel hotel;
    private HotelRoomType hotelRoomType;
    private HotelRoomPrice hotelRoomPrice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        requestDTO = new HotelSearchRequestDTO();
        requestDTO.setLocation("Paris");
        requestDTO.setCheckInDate(LocalDate.of(2025, 6, 15));
        requestDTO.setCheckOutDate(LocalDate.of(2025, 6, 20));
        requestDTO.setGuests(2);

        hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Test Hotel");
        hotel.setLocation("Paris");

        hotelRoomType = new HotelRoomType();
        hotelRoomType.setId(101L);
        hotelRoomType.setName("Deluxe Suite");
        hotelRoomType.setCapacity(2);
        hotelRoomType.setBasePrice(new BigDecimal("200.00"));

        hotelRoomPrice = new HotelRoomPrice();
        hotelRoomPrice.setId(201L);
        hotelRoomPrice.setHotelRoomType(hotelRoomType);
        hotelRoomPrice.setPrice(new BigDecimal("50.00"));
        hotelRoomPrice.setStartDate(LocalDateTime.of(2025, 6, 16, 0, 0));
        hotelRoomPrice.setEndDate(LocalDateTime.of(2025, 6, 19, 0, 0));
    }

    @Test
    void testGetHotelPrices_withMaxPrice() {
        // Given
        requestDTO.setMaxPrice(new BigDecimal("1200.00"));

        List<Hotel> hotels = new ArrayList<>();
        hotels.add(hotel);

        List<HotelRoomType> hotelRoomTypes = new ArrayList<>();
        hotelRoomTypes.add(hotelRoomType);

        List<HotelRoomPrice> hotelRoomPrices = new ArrayList<>();
        hotelRoomPrices.add(hotelRoomPrice);

        when(hotelRepository.findAllByLocation("Paris")).thenReturn(hotels);
        when(hotelRoomTypeRepository.findAllByHotelId(hotel.getId())).thenReturn(hotelRoomTypes);
        when(hotelRoomPriceRepository.findPricesWithOverlappingDates(
                hotelRoomType.getId(),
                requestDTO.getCheckInDate().atStartOfDay(),
                requestDTO.getCheckOutDate().atStartOfDay()
        )).thenReturn(hotelRoomPrices);

        // When
        List<HotelOfferDTO> result = hotelInformationService.getHotelPrices(requestDTO);

        // Then
        assertEquals(1, result.size());
        HotelOfferDTO dto = result.get(0);
        assertEquals(1L, dto.getHotelId());
        assertEquals("Test Hotel", dto.getHotelName());
        assertEquals("Paris", dto.getLocation());
        assertEquals(101L, dto.getHotelRoomTypeId());
        assertEquals(2, dto.getCapacity());
        assertEquals("Deluxe Suite", dto.getHotelRoomTypeName());
        assertEquals(new BigDecimal("1150.00"), dto.getTotalPrice());
    }

    @Test
    void testGetHotelPrices_withNoMaxPrice() {
        // Given
        requestDTO.setMaxPrice(null);

        List<Hotel> hotels = List.of(hotel);
        List<HotelRoomType> hotelRoomTypes = List.of(hotelRoomType);
        List<HotelRoomPrice> hotelRoomPrices = List.of(hotelRoomPrice);

        when(hotelRepository.findAllByLocation("Paris")).thenReturn(hotels);
        when(hotelRoomTypeRepository.findAllByHotelId(hotel.getId())).thenReturn(hotelRoomTypes);
        when(hotelRoomPriceRepository.findPricesWithOverlappingDates(
                hotelRoomType.getId(),
                requestDTO.getCheckInDate().atStartOfDay(),
                requestDTO.getCheckOutDate().atStartOfDay()
        )).thenReturn(hotelRoomPrices);

        // When
        List<HotelOfferDTO> result = hotelInformationService.getHotelPrices(requestDTO);

        // Then
        assertEquals(1, result.size());
        HotelOfferDTO dto = result.get(0);
        assertEquals(1L, dto.getHotelId());
        assertEquals("Test Hotel", dto.getHotelName());
        assertEquals("Paris", dto.getLocation());
        assertEquals(101L, dto.getHotelRoomTypeId());
        assertEquals(2, dto.getCapacity());
        assertEquals("Deluxe Suite", dto.getHotelRoomTypeName());
        assertEquals(new BigDecimal("1150.00"), dto.getTotalPrice());
    }
}