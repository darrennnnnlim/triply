package com.example.triply.core.hotel.mapper;

import com.example.triply.core.hotel.model.dto.HotelAddonDTO;
import com.example.triply.core.hotel.model.entity.Hotel;
import com.example.triply.core.hotel.model.entity.HotelAddon;
import com.example.triply.core.hotel.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HotelAddonMapperTest {

    private HotelRepository hotelRepository;
    private HotelAddonMapper mapper;

    @BeforeEach
    public void setUp() {
        hotelRepository = Mockito.mock(HotelRepository.class);
        mapper = new HotelAddonMapper(hotelRepository);
    }

    @Test
    public void testToDto_NullEntity_ReturnsNull() {
        HotelAddonDTO dto = mapper.toDto((HotelAddon) null);
        assertNull(dto, "Mapping null entity should return null DTO");
    }

    @Test
    public void testToDto_ValidEntity_ReturnsDto() {
        Hotel hotel = new Hotel();
        hotel.setId(1L);
        HotelAddon entity = new HotelAddon();
        entity.setId(2L);
        entity.setHotel(hotel);
        entity.setName("Breakfast");
        entity.setPrice(new BigDecimal(20.0));

        HotelAddonDTO dto = mapper.toDto(entity);

        assertNotNull(dto, "DTO should not be null");
        assertEquals(entity.getId(), dto.getId(), "IDs should match");
        assertEquals(entity.getHotel().getId(), dto.getHotelId(), "Hotel IDs should match");
        assertEquals(entity.getName(), dto.getName(), "Names should match");
        assertEquals(entity.getPrice(), dto.getPrice(), "Prices should match");
    }

    @Test
    public void testToEntity_NullDto_ReturnsNull() {
        HotelAddon entity = mapper.toEntity((HotelAddonDTO) null);
        assertNull(entity, "Mapping null DTO should return null entity");
    }

    @Test
    public void testToEntity_ValidDto_WithExistingHotel_ReturnsEntity() {
        HotelAddonDTO dto = new HotelAddonDTO();
        dto.setId(2L);
        dto.setHotelId(1L);
        dto.setName("Breakfast");
        dto.setPrice(new BigDecimal(20.0));

        Hotel hotel = new Hotel();
        hotel.setId(1L);

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        HotelAddon entity = mapper.toEntity(dto);

        assertNotNull(entity, "Entity should not be null");
        assertEquals(dto.getId(), entity.getId(), "IDs should match");
        assertEquals(hotel, entity.getHotel(), "Hotels should match");
        assertEquals(dto.getName(), entity.getName(), "Names should match");
        assertEquals(dto.getPrice(), entity.getPrice(), "Prices should match");

        verify(hotelRepository, times(1)).findById(1L);
    }

    @Test
    public void testToEntity_ValidDto_WithNonExistingHotel_ReturnsEntityWithNullHotel() {
        HotelAddonDTO dto = new HotelAddonDTO();
        dto.setId(2L);
        dto.setHotelId(1L);
        dto.setName("Breakfast");
        dto.setPrice(new BigDecimal(20.0));

        when(hotelRepository.findById(1L)).thenReturn(Optional.empty());

        HotelAddon entity = mapper.toEntity(dto);

        assertNotNull(entity, "Entity should not be null");
        assertEquals(dto.getId(), entity.getId(), "IDs should match");
        assertNull(entity.getHotel(), "Hotel should be null when not found");
        assertEquals(dto.getName(), entity.getName(), "Names should match");
        assertEquals(dto.getPrice(), entity.getPrice(), "Prices should match");

        verify(hotelRepository, times(1)).findById(1L);
    }
}