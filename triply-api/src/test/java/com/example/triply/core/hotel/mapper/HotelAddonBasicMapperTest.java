package com.example.triply.core.hotel.mapper;

import com.example.triply.core.hotel.model.dto.HotelAddonBasicDTO;
import com.example.triply.core.hotel.model.entity.HotelAddon;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HotelAddonBasicMapperTest {

    private final HotelAddonBasicMapper mapper = new HotelAddonBasicMapper();

    @Test
    public void testToDto_NullEntity_ReturnsNull() {
        HotelAddonBasicDTO dto = mapper.toDto((HotelAddon) null);
        assertNull(dto, "Mapping null entity should return null DTO");
    }

    @Test
    public void testToDto_ValidEntity_ReturnsDto() {
        HotelAddon entity = new HotelAddon();
        entity.setId(1L);
        entity.setName("Extra Bed");

        HotelAddonBasicDTO dto = mapper.toDto(entity);

        assertNotNull(dto, "DTO should not be null");
        assertEquals(entity.getId(), dto.getId(), "IDs should match");
        assertEquals(entity.getName(), dto.getName(), "Names should match");
    }

    @Test
    public void testToEntity_NullDto_ReturnsNull() {
        HotelAddon entity = mapper.toEntity((HotelAddonBasicDTO) null);
        assertNull(entity, "Mapping null DTO should return null entity");
    }

    @Test
    public void testToEntity_ValidDto_ReturnsEntity() {
        HotelAddonBasicDTO dto = new HotelAddonBasicDTO();
        dto.setId(1L);
        dto.setName("Extra Bed");

        HotelAddon entity = mapper.toEntity(dto);

        assertNotNull(entity, "Entity should not be null");
        assertEquals(dto.getId(), entity.getId(), "IDs should match");
        assertEquals(dto.getName(), entity.getName(), "Names should match");
    }
}