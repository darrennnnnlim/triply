package com.example.triply.core.hotel.mapper;

import com.example.triply.core.hotel.model.dto.HotelDTO;
import com.example.triply.core.hotel.model.entity.Hotel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HotelMapperTest {

    private final HotelMapper mapper = new HotelMapper();

    @Test
    public void testToDto_NullEntity_ReturnsNull() {
        HotelDTO dto = mapper.toDto((Hotel) null);
        assertNull(dto, "Mapping null entity should return null DTO");
    }

    @Test
    public void testToDto_ValidEntity_ReturnsDto() {
        Hotel entity = new Hotel();
        entity.setId(1L);
        entity.setName("Grand Hotel");
        entity.setLocation("New York");
        entity.setDescription("A luxurious hotel in the heart of the city.");

        HotelDTO dto = mapper.toDto(entity);

        assertNotNull(dto, "DTO should not be null");
        assertEquals(entity.getId(), dto.getId(), "IDs should match");
        assertEquals(entity.getName(), dto.getName(), "Names should match");
        assertEquals(entity.getLocation(), dto.getLocation(), "Locations should match");
        assertEquals(entity.getDescription(), dto.getDescription(), "Descriptions should match");
    }

    @Test
    public void testToEntity_NullDto_ReturnsNull() {
        Hotel entity = mapper.toEntity((HotelDTO) null);
        assertNull(entity, "Mapping null DTO should return null entity");
    }

    @Test
    public void testToEntity_ValidDto_ReturnsEntity() {
        HotelDTO dto = new HotelDTO();
        dto.setId(1L);
        dto.setName("Grand Hotel");
        dto.setLocation("New York");
        dto.setDescription("A luxurious hotel in the heart of the city.");

        Hotel entity = mapper.toEntity(dto);

        assertNotNull(entity, "Entity should not be null");
        assertEquals(dto.getId(), entity.getId(), "IDs should match");
        assertEquals(dto.getName(), entity.getName(), "Names should match");
        assertEquals(dto.getLocation(), entity.getLocation(), "Locations should match");
        assertEquals(dto.getDescription(), entity.getDescription(), "Descriptions should match");
    }
}