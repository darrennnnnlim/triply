package com.example.triply.core.pricing.flight.implementation;

import com.example.triply.core.flight.mapper.FlightPriceMapper;
import com.example.triply.core.flight.model.dto.FlightPriceDTO;
import com.example.triply.core.flight.model.entity.FlightPrice;
import com.example.triply.core.flight.repository.FlightPriceRepository;
import com.example.triply.core.pricing.flight.notification.FlightPriceListener;
import com.example.triply.core.pricing.flight.notification.FlightPriceWritePublisherImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FlightPriceWriteFacadeServiceImplTest {

    @Mock
    private FlightPriceRepository flightPriceRepository;

    @Mock
    private FlightPriceMapper flightPriceMapper;

    @Mock
    private FlightPriceWritePublisherImpl publisher;

    @InjectMocks
    private FlightPriceWriteFacadeServiceImpl flightPriceWriteService;

    private FlightPrice flightPrice;
    private FlightPriceDTO flightPriceDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        flightPrice = new FlightPrice();
        flightPrice.setId(100L);

        flightPriceDTO = new FlightPriceDTO();
        flightPriceDTO.setId(100L);
    }

    @Test
    void testUpdateExistingFlightPrice_FlightPriceFound() {
        // Given
        Long flightId = 1L;
        Long flightClassId = 2L;
        BigDecimal newBasePrice = new BigDecimal("199.99");

        when(flightPriceRepository.findByFlightAndFlightClass(flightId, flightClassId))
                .thenReturn(Optional.of(flightPrice));

        // Old version of flightPrice
        when(flightPriceMapper.toDto(flightPrice)).thenReturn(flightPriceDTO);

        // New version of flightPrice
        FlightPrice updatedFlightPrice = new FlightPrice(flightPrice);
        updatedFlightPrice.setBasePrice(newBasePrice);
        FlightPriceDTO updatedFlightPriceDTO = new FlightPriceDTO();
        updatedFlightPriceDTO.setId(100L);
        updatedFlightPriceDTO.setBasePrice(newBasePrice);

        when(flightPriceMapper.toDto(any(FlightPrice.class))).thenReturn(updatedFlightPriceDTO);

        // When
        List<FlightPriceDTO> newPrices = flightPriceWriteService.updateExistingFlightPrice(flightId, flightClassId, newBasePrice);

        // Then
        assertEquals(1, newPrices.size());
        assertEquals(newBasePrice, newPrices.get(0).getBasePrice());
        verify(flightPriceRepository).save(any(FlightPrice.class));
        verify(publisher).publish(anyList(), anyList());
    }

    @Test
    void testUpdateExistingFlightPrice_FlightPriceNotFound() {
        // Given
        Long flightId = 1L;
        Long flightClassId = 2L;
        BigDecimal newBasePrice = new BigDecimal("199.99");

        when(flightPriceRepository.findByFlightAndFlightClass(flightId, flightClassId))
                .thenReturn(Optional.empty());

        // When
        List<FlightPriceDTO> newPrices = flightPriceWriteService.updateExistingFlightPrice(flightId, flightClassId, newBasePrice);

        // Then
        assertTrue(newPrices.isEmpty());
        verify(flightPriceRepository, never()).save(any(FlightPrice.class));
        verify(publisher, never()).publish(anyList(), anyList());
    }

    @Test
    void testInsertNewFlightPrice() {
        // Given
        Long flightId = 10L;
        Long flightClassId = 20L;
        BigDecimal newBasePrice = new BigDecimal("150.00");
        BigDecimal discount = new BigDecimal("10.00");
        BigDecimal surgeMultiplier = new BigDecimal("1.25");

        FlightPrice newFlightPrice = new FlightPrice();
        when(flightPriceRepository.save(newFlightPrice)).thenAnswer(invocation -> {
            FlightPrice saved = invocation.getArgument(0);
            saved.setId(999L);
            return saved;
        });

        FlightPriceDTO newFlightPriceDTO = new FlightPriceDTO();
        newFlightPriceDTO.setId(999L);
        when(flightPriceMapper.toDto(any(FlightPrice.class))).thenReturn(newFlightPriceDTO);

        // When
        List<FlightPriceDTO> priceDTOList = flightPriceWriteService.insertNewFlightPrice(
            flightId,
            flightClassId,
            newBasePrice,
            discount,
            surgeMultiplier
        );

        // Then
        assertEquals(1, priceDTOList.size());
        assertEquals(999L, priceDTOList.get(0).getId());
        verify(flightPriceRepository).save(any(FlightPrice.class));
        verify(publisher).publish(anyList(), anyList());
    }

    @Test
    void testAddPriceListener() {
        // Given
        FlightPriceListener listener = mock(FlightPriceListener.class);

        // When
        flightPriceWriteService.addPriceListener(listener);

        // Then
        verify(publisher).addListener(listener);
    }

    @Test
    void testRemovePriceListener() {
        // Given
        FlightPriceListener listener = mock(FlightPriceListener.class);

        // When
        flightPriceWriteService.removePriceListener(listener);

        // Then
        verify(publisher).removeListener(listener);
    }
}