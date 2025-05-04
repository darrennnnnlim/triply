package com.example.triply.core.hotel.mapper;

import com.example.triply.core.hotel.model.dto.HotelRoomTypeDTO;
import com.example.triply.core.hotel.model.entity.Hotel;
import com.example.triply.core.hotel.model.entity.HotelRoomType;
import com.example.triply.core.hotel.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HotelRoomTypeMapperTest {

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private HotelRoomTypeMapper hotelRoomTypeMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testToDto_ValidEntity_ReturnsDto() {
        Hotel hotel = new Hotel();
        hotel.setId(1L);

        HotelRoomType entity = new HotelRoomType();
        entity.setId(2L);
        entity.setHotel(hotel);
        entity.setName("Deluxe Suite");
        entity.setBasePrice(BigDecimal.valueOf(200.00));
        entity.setCapacity(4);

        HotelRoomTypeDTO dto = hotelRoomTypeMapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getHotel().getId(), dto.getHotelId());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getBasePrice(), dto.getBasePrice());
        assertEquals(entity.getCapacity(), dto.getCapacity());
    }

    @Test
    public void testToEntity_HotelExists_ReturnsEntity() {
        HotelRoomTypeDTO dto = new HotelRoomTypeDTO();
        dto.setId(2L);
        dto.setHotelId(1L);
        dto.setName("Deluxe Suite");
        dto.setBasePrice(BigDecimal.valueOf(200.00));
        dto.setCapacity(4);

        Hotel hotel = new Hotel();
        hotel.setId(dto.getHotelId());

        when(hotelRepository.findById(dto.getHotelId())).thenReturn(Optional.of(hotel));

        HotelRoomType entity = hotelRoomTypeMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertEquals(hotel, entity.getHotel());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getBasePrice(), entity.getBasePrice());
        assertEquals(dto.getCapacity(), entity.getCapacity());
    }

    @Test
    public void testToEntity_HotelDoesNotExist_ReturnsEntityWithNullHotel() {
        HotelRoomTypeDTO dto = new HotelRoomTypeDTO();
        dto.setId(2L);
        dto.setHotelId(1L);
        dto.setName("Deluxe Suite");
        dto.setBasePrice(BigDecimal.valueOf(200.00));
        dto.setCapacity(4);

        when(hotelRepository.findById(dto.getHotelId())).thenReturn(Optional.empty());

        HotelRoomType entity = hotelRoomTypeMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getId());
        assertNull(entity.getHotel());
        assertEquals(dto.getName(), entity.getName());
        assertEquals(dto.getBasePrice(), entity.getBasePrice());
        assertEquals(dto.getCapacity(), entity.getCapacity());
    }
}
