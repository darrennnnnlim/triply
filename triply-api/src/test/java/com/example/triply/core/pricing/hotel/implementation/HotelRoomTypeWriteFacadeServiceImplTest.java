package com.example.triply.core.pricing.hotel.implementation;

import com.example.triply.core.hotel.mapper.HotelRoomTypeMapper;
import com.example.triply.core.hotel.model.dto.HotelRoomTypeDTO;
import com.example.triply.core.hotel.model.entity.HotelRoomType;
import com.example.triply.core.hotel.repository.HotelRoomTypeRepository;
import com.example.triply.core.pricing.hotel.notification.HotelRoomTypeListener;
import com.example.triply.core.pricing.hotel.notification.HotelRoomTypeWritePublisherImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HotelRoomTypeWriteFacadeServiceImplTest {

    @Mock
    private HotelRoomTypeRepository hotelRoomTypePriceRepository;

    @Mock
    private HotelRoomTypeMapper hotelRoomTypePriceMapper;

    @Mock
    private HotelRoomTypeWritePublisherImpl publisher;

    @InjectMocks
    private HotelRoomTypeWriteFacadeServiceImpl hotelRoomTypeWriteService;

    private HotelRoomType hotelRoomType;
    private HotelRoomTypeDTO hotelRoomTypeDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        hotelRoomType = new HotelRoomType();
        hotelRoomType.setId(100L);

        hotelRoomTypeDTO = new HotelRoomTypeDTO();
        hotelRoomTypeDTO.setId(100L);
    }

    @Test
    void testUpdateExistingHotelRoomType_HotelRoomTypeFound() {
        // Given
        Long hotelRoomTypeId = 1L;
        BigDecimal newBasePrice = new BigDecimal("199.99");

        when(hotelRoomTypePriceRepository.findById(hotelRoomTypeId))
                .thenReturn(Optional.of(hotelRoomType));

        // Old hotelRoomType
        when(hotelRoomTypePriceMapper.toDto(hotelRoomType)).thenReturn(hotelRoomTypeDTO);

        // New hotelRoomType
        HotelRoomType updatedHotelRoomType = new HotelRoomType(hotelRoomType);
        updatedHotelRoomType.setBasePrice(newBasePrice.setScale(2, RoundingMode.HALF_EVEN));
        HotelRoomTypeDTO updatedHotelRoomTypeDTO = new HotelRoomTypeDTO();
        updatedHotelRoomTypeDTO.setId(100L);
        updatedHotelRoomTypeDTO.setBasePrice(newBasePrice.setScale(2, RoundingMode.HALF_EVEN));

        when(hotelRoomTypePriceMapper.toDto(any(HotelRoomType.class))).thenReturn(updatedHotelRoomTypeDTO);

        // When
        List<HotelRoomTypeDTO> newPrices = hotelRoomTypeWriteService.updateExistingHotelRoomType(hotelRoomTypeId, newBasePrice);

        // Then
        assertEquals(1, newPrices.size());
        assertEquals(newBasePrice.setScale(2, RoundingMode.HALF_EVEN), newPrices.get(0).getBasePrice());
        verify(hotelRoomTypePriceRepository).save(any(HotelRoomType.class));
        verify(publisher).publish(anyList(), anyList());
    }

    @Test
    void testUpdateExistingHotelRoomType_HotelRoomTypeNotFound() {
        // Given
        Long hotelRoomTypeId = 1L;
        BigDecimal newBasePrice = new BigDecimal("199.99");

        when(hotelRoomTypePriceRepository.findById(hotelRoomTypeId))
                .thenReturn(Optional.empty());

        // When
        List<HotelRoomTypeDTO> newPrices = hotelRoomTypeWriteService.updateExistingHotelRoomType(hotelRoomTypeId, newBasePrice);

        // Then
        assertTrue(newPrices.isEmpty());
        verify(hotelRoomTypePriceRepository, never()).save(any(HotelRoomType.class));
        verify(publisher, never()).publish(anyList(), anyList());
    }

    @Test
    void testInsertNewHotelRoomType() {
        // Given
        Long hotelId = 10L;
        BigDecimal newBasePrice = new BigDecimal("150.00");
        BigDecimal discount = new BigDecimal("10.00");
        BigDecimal surgeMultiplier = new BigDecimal("1.25");

        HotelRoomType newHotelRoomType = new HotelRoomType();
        when(hotelRoomTypePriceRepository.save(newHotelRoomType)).thenAnswer(invocation -> {
            HotelRoomType saved = invocation.getArgument(0);
            saved.setId(999L);
            return saved;
        });

        HotelRoomTypeDTO newHotelRoomTypeDTO = new HotelRoomTypeDTO();
        newHotelRoomTypeDTO.setId(999L);
        when(hotelRoomTypePriceMapper.toDto(any(HotelRoomType.class))).thenReturn(newHotelRoomTypeDTO);

        // When
        List<HotelRoomTypeDTO> priceDTOList = hotelRoomTypeWriteService.insertNewHotelRoomType(
            hotelId,
            newBasePrice,
            discount,
            surgeMultiplier
        );

        // Then
        assertEquals(1, priceDTOList.size());
        assertEquals(999L, priceDTOList.get(0).getId());
        verify(hotelRoomTypePriceRepository).save(any(HotelRoomType.class));
        verify(publisher).publish(anyList(), anyList());
    }

    @Test
    void testAddPriceListener() {
        // Given
        HotelRoomTypeListener listener = mock(HotelRoomTypeListener.class);

        // When
        hotelRoomTypeWriteService.addPriceListener(listener);

        // Then
        verify(publisher).addListener(listener);
    }

    @Test
    void testRemovePriceListener() {
        // Given
        HotelRoomTypeListener listener = mock(HotelRoomTypeListener.class);

        // When
        hotelRoomTypeWriteService.removePriceListener(listener);

        // Then
        verify(publisher).removeListener(listener);
    }
}