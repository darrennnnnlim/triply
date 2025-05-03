package com.example.triply.core.hotel.mapper;

import com.example.triply.core.hotel.model.dto.HotelRoomTypeBasicDTO;
import com.example.triply.core.hotel.model.entity.Hotel;
import com.example.triply.core.hotel.model.entity.HotelRoomType;
import com.example.triply.core.hotel.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HotelRoomTypeBasicMapperTest {

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private HotelRoomTypeBasicMapper mapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testToDto_NullEntity_ReturnsNull() {
        HotelRoomTypeBasicDTO dto = mapper.toDto((HotelRoomType) null);
        assertNull(dto, "Mapping null entity should return null DTO");
    }

    @Test
    public void testToDto_ValidEntity_ReturnsDto() {
        Hotel hotel = new Hotel();
        hotel.setId(1L);

        HotelRoomType entity = new HotelRoomType();
        entity.setId(1L);
        entity.setHotel(hotel);
        entity.setName("Deluxe Suite");
        entity.setCapacity(4);

        HotelRoomTypeBasicDTO dto = mapper.toDto(entity);

        assertNotNull(dto, "DTO should not be null");
        assertEquals(entity.getId(), dto.getId(), "IDs should match");
        assertEquals(entity.getHotel().getId(), dto.getHotelId(), "Hotel IDs should match");
        assertEquals(entity.getName(), dto.getName(), "Names should match");
        assertEquals(entity.getCapacity(), dto.getCapacity(), "Capacities should match");

    }

    @Test
    public void testToEntity_NullDto_ReturnsNull() {
        HotelRoomType entity = mapper.toEntity((HotelRoomTypeBasicDTO) null);
        assertNull(entity, "Mapping null DTO should return null entity");
    }

    @Test
    public void testToEntity_ValidDto_ReturnsEntity() {
        HotelRoomTypeBasicDTO dto = new HotelRoomTypeBasicDTO();
        dto.setId(1L);
        dto.setHotelId(1L);
        dto.setName("Deluxe Suite");
        dto.setCapacity(4);

        Hotel hotel = new Hotel();
        hotel.setId(1L);

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        HotelRoomType entity = mapper.toEntity(dto);

        assertNotNull(entity, "Entity should not be null");
        assertEquals(dto.getId(), entity.getId(), "IDs should match");
        assertEquals(hotel, entity.getHotel(), "Hotel should match");
        assertEquals(dto.getName(), entity.getName(), "Names should match");
        assertEquals(dto.getCapacity(), entity.getCapacity(), "Capacities should match");

        verify(hotelRepository, times(1)).findById(1L);
    }

    @Test
    public void testToEntity_ValidDto_WithNonExistingHotel_ReturnsEntityWithNullHotel() {
        HotelRoomTypeBasicDTO dto = new HotelRoomTypeBasicDTO();
        dto.setId(1L);
        dto.setHotelId(1L);
        dto.setName("Deluxe Suite");
        dto.setCapacity(4);

        when(hotelRepository.findById(1L)).thenReturn(Optional.empty());

        HotelRoomType entity = mapper.toEntity(dto);

        assertNotNull(entity, "Entity should not be null");
        assertEquals(dto.getId(), entity.getId(), "IDs should match");
        assertNull(entity.getHotel(), "Hotel should be null when not found");
        assertEquals(dto.getName(), entity.getName(), "Names should match");
        assertEquals(dto.getCapacity(), entity.getCapacity(), "Capacities should match");

        verify(hotelRepository, times(1)).findById(1L);
    }
}