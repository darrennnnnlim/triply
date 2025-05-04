package com.example.triply.core.pricethreshold.service.impl;

import com.example.triply.core.auth.entity.User;
import com.example.triply.core.auth.repository.UserRepository;
import com.example.triply.core.hotel.model.entity.Hotel;
import com.example.triply.core.pricethreshold.dto.CreatePriceThresholdRequest;
import com.example.triply.core.pricethreshold.dto.PriceThresholdDTO;
import com.example.triply.core.pricethreshold.entity.PriceThreshold;
import com.example.triply.core.pricethreshold.mapper.PriceThresholdMapper;
import com.example.triply.core.pricethreshold.repository.PriceThresholdRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class PriceThresholdServiceImplTest {

    @Mock
    private PriceThresholdRepository thresholdRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private PriceThresholdMapper mapper;

    @InjectMocks
    private PriceThresholdServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createThreshold_shouldCreateSuccessfully() {
        CreatePriceThresholdRequest request = new CreatePriceThresholdRequest();
        request.setUserId(1L);
        request.setThresholdPrice(new BigDecimal("100"));
        request.setConceptId(1L);
        request.setConceptType("FLIGHT");
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(3));

        User user = new User();
        user.setId(1L);

        PriceThreshold savedEntity = new PriceThreshold();
        savedEntity.setId(1L);
        savedEntity.setUser(user);

        when(userRepo.getReferenceById(1L)).thenReturn(user);
        when(thresholdRepo.save(any())).thenReturn(savedEntity);
        when(mapper.toDto(savedEntity)).thenReturn(new PriceThresholdDTO());

        PriceThresholdDTO result = service.createThreshold(request);
        assertThat(result).isNotNull();

        verify(thresholdRepo).save(any());
        verify(mapper).toDto(savedEntity);
    }

    @Test
    void createThreshold_shouldThrowException_whenThresholdPriceIsInvalid() {
        CreatePriceThresholdRequest request = new CreatePriceThresholdRequest();
        request.setThresholdPrice(BigDecimal.ZERO);

        assertThatThrownBy(() -> service.createThreshold(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getThresholdsByUser_shouldReturnThresholds() {
        User user = new User();
        user.setUsername("john");

        PriceThreshold threshold = new PriceThreshold();
        threshold.setId(1L);

        when(userRepo.findByUsername("john")).thenReturn(Optional.of(user));
        when(thresholdRepo.findByUser(user)).thenReturn(List.of(threshold));
        when(mapper.toDto(threshold)).thenReturn(new PriceThresholdDTO());

        List<PriceThresholdDTO> result = service.getThresholdsByUser("john");
        assertThat(result).hasSize(1);
    }

    @Test
    void getThresholdsByUser_shouldThrow_whenUserNotFound() {
        when(userRepo.findByUsername("not_found")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getThresholdsByUser("not_found"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteThreshold_shouldDelete_whenAuthorized() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");

        PriceThreshold threshold = new PriceThreshold();
        threshold.setId(2L);
        threshold.setUser(user);

        when(userRepo.findByUsername("john")).thenReturn(Optional.of(user));
        when(thresholdRepo.findById(2L)).thenReturn(Optional.of(threshold));

        service.deleteThreshold(2L, "john");

        verify(thresholdRepo).delete(threshold);
    }

    @Test
    void deleteThreshold_shouldThrow_whenUserNotFound() {
        when(userRepo.findByUsername("not_found")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteThreshold(2L, "not_found"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteThreshold_shouldThrow_whenThresholdNotFound() {
        User user = new User();
        user.setId(1L);

        when(userRepo.findByUsername("john")).thenReturn(Optional.of(user));
        when(thresholdRepo.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteThreshold(2L, "john"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteThreshold_shouldThrow_whenUnauthorized() {
        User user = new User();
        user.setId(1L);

        User anotherUser = new User();
        anotherUser.setId(2L);

        PriceThreshold threshold = new PriceThreshold();
        threshold.setUser(anotherUser);

        when(userRepo.findByUsername("john")).thenReturn(Optional.of(user));
        when(thresholdRepo.findById(2L)).thenReturn(Optional.of(threshold));

        assertThatThrownBy(() -> service.deleteThreshold(2L, "john"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void findUsersToNotifyForFlight_shouldReturnUsers() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setEmail("john@example.com");

        PriceThreshold threshold = new PriceThreshold();
        threshold.setUser(user);

        when(thresholdRepo.findByConceptTypeAndConceptIdAndThresholdPriceGreaterThanEqual("FLIGHT", 1L, new BigDecimal("99")))
                .thenReturn(List.of(threshold));

        List<User> result = service.findUsersToNotifyForFlight(1L, new BigDecimal("99"));
        assertThat(result).hasSize(1);
    }

    @Test
    void findUsersToNotifyForHotel_shouldReturnUsers() {
        User user = new User();
        user.setId(1L);

        Hotel hotel = new Hotel();
        hotel.setId(10L);

        PriceThreshold threshold = new PriceThreshold();
        threshold.setUser(user);

        when(thresholdRepo.findByConceptTypeAndConceptIdAndThresholdPriceGreaterThanEqual("HOTEL", 10L, new BigDecimal("88")))
                .thenReturn(List.of(threshold));

        List<User> result = service.findUsersToNotifyForHotel(hotel, new BigDecimal("88"));
        assertThat(result).hasSize(1);
    }
}
