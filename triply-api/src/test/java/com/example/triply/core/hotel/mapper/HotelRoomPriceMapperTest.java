package com.example.triply.core.hotel.mapper;

import com.example.triply.core.hotel.model.dto.HotelRoomPriceDTO;
import com.example.triply.core.hotel.model.entity.HotelRoomPrice;
import com.example.triply.core.hotel.model.entity.HotelRoomType;
import com.example.triply.core.hotel.repository.HotelRoomTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HotelRoomPriceMapperTest {

    private HotelRoomPriceMapper mapper;

    @BeforeEach
    public void setUp() {
        mapper = new HotelRoomPriceMapper();
    }

    @Test
    public void testToDto_ValidEntity_ReturnsDto() {
        HotelRoomType hotelRoomType = new HotelRoomType();
        hotelRoomType.setId(1L);

        HotelRoomPrice entity = new HotelRoomPrice();
        entity.setId(2L);
        entity.setHotelRoomType(hotelRoomType);
        entity.setStartDate(LocalDate.of(2023, 1, 1).atStartOfDay());
        entity.setEndDate(LocalDate.of(2023, 12, 31).atStartOfDay());
        entity.setPrice(new BigDecimal("100.00"));

        HotelRoomPriceDTO dto = mapper.toDto(entity);

        assertNotNull(dto, "DTO should not be null");
        assertEquals(entity.getId(), dto.getId(), "IDs should match");
        assertEquals(entity.getHotelRoomType().getId(), dto.getHotelRoomTypeId(), "HotelRoomType IDs should match");
        assertEquals(entity.getStartDate(), dto.getStartDate(), "Start dates should match");
        assertEquals(entity.getEndDate(), dto.getEndDate(), "End dates should match");
        assertEquals(entity.getPrice(), dto.getPrice(), "Prices should match");
    }
}