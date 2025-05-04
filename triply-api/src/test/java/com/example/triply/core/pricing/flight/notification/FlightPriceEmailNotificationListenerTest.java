package com.example.triply.core.pricing.flight.notification;

import com.example.triply.common.service.EmailService;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.flight.model.dto.FlightClassDTO;
import com.example.triply.core.flight.model.dto.FlightDTO;
import com.example.triply.core.flight.model.dto.FlightPriceDTO;
import com.example.triply.core.pricethreshold.service.PriceThresholdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FlightPriceEmailNotificationListenerTest {

    @Mock
    private EmailService emailService;

    @Mock
    private PriceThresholdService priceThresholdService;

    @InjectMocks
    private FlightPriceEmailNotificationListener listener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private FlightPriceDTO createFlightPriceDTO(Long flightId, Long classId, LocalDateTime departureDate,
                                                BigDecimal base, BigDecimal discount, BigDecimal surge) {
        FlightPriceDTO dto = new FlightPriceDTO();
        FlightDTO flight = new FlightDTO();
        flight.setId(flightId);
        dto.setFlightDTO(flight);

        FlightClassDTO flightClass = new FlightClassDTO();
        flightClass.setId(classId);
        dto.setFlightClassDTO(flightClass);

        dto.setDepartureDate(departureDate);
        dto.setBasePrice(base);
        dto.setDiscount(discount);
        dto.setSurgeMultiplier(surge);
        dto.setFlightNumber("SQ001");
        dto.setFlightClassName("Economy");
        dto.setOrigin("SIN");
        dto.setDestination("NRT");

        return dto;
    }

    @Test
    void shouldSendEmailWhenPriceDrops() {
        FlightPriceDTO oldPrice = createFlightPriceDTO(1L, 1L, LocalDateTime.now(), new BigDecimal("500"), BigDecimal.ONE, BigDecimal.ONE);
        FlightPriceDTO newPrice = createFlightPriceDTO(1L, 1L, oldPrice.getDepartureDate(), new BigDecimal("400"), BigDecimal.ONE, BigDecimal.ONE);

        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setEmail("john@example.com");

        when(priceThresholdService.findUsersToNotifyForFlight(eq(1L), any(BigDecimal.class)))
                .thenReturn(List.of(user));

        FlightPriceWriteEvent event = new FlightPriceWriteEvent(List.of(oldPrice), List.of(newPrice));
        listener.onPriceUpdate(event);

        verify(emailService).sendEmail(eq("john@example.com"), contains("Price Drop Alert!"), contains("john"));
    }

    @Test
    void shouldSkipWhenNewPriceFlightIdIsNull() {
        FlightPriceDTO oldPrice = createFlightPriceDTO(1L, 1L, LocalDateTime.now(), new BigDecimal("500"), BigDecimal.ONE, BigDecimal.ONE);
        FlightPriceDTO newPrice = createFlightPriceDTO(null, 1L, oldPrice.getDepartureDate(), new BigDecimal("400"), BigDecimal.ONE, BigDecimal.ONE);
        newPrice.setFlightDTO(null); // simulate missing flight

        FlightPriceWriteEvent event = new FlightPriceWriteEvent(List.of(oldPrice), List.of(newPrice));
        listener.onPriceUpdate(event);

        verifyNoInteractions(emailService);
    }

    @Test
    void shouldSkipWhenPriceIncreases() {
        FlightPriceDTO oldPrice = createFlightPriceDTO(1L, 1L, LocalDateTime.now(), new BigDecimal("400"), BigDecimal.ONE, BigDecimal.ONE);
        FlightPriceDTO newPrice = createFlightPriceDTO(1L, 1L, oldPrice.getDepartureDate(), new BigDecimal("500"), BigDecimal.ONE, BigDecimal.ONE);

        FlightPriceWriteEvent event = new FlightPriceWriteEvent(List.of(oldPrice), List.of(newPrice));
        listener.onPriceUpdate(event);

        verifyNoInteractions(emailService);
    }

    @Test
    void shouldDoNothingWhenNewPriceListIsEmpty() {
        FlightPriceWriteEvent event = new FlightPriceWriteEvent(List.of(), List.of());
        listener.onPriceUpdate(event);

        verifyNoInteractions(emailService);
    }
}
