package com.example.triply.core.pricethreshold.service.impl;

import com.example.triply.core.auth.entity.User;
import com.example.triply.core.auth.repository.UserRepository;
import com.example.triply.core.flight.model.entity.Flight;
import com.example.triply.core.flight.repository.FlightRepository;
import com.example.triply.core.hotel.model.entity.Hotel;
import com.example.triply.core.hotel.repository.HotelRepository;
import com.example.triply.core.pricethreshold.dto.CreatePriceThresholdRequest;
import com.example.triply.core.pricethreshold.dto.PriceThresholdDTO;
import com.example.triply.core.pricethreshold.entity.PriceThreshold;
import com.example.triply.core.pricethreshold.mapper.PriceThresholdMapper; // Assuming mapper exists
import com.example.triply.core.pricethreshold.repository.PriceThresholdRepository;
import com.example.triply.core.pricethreshold.service.PriceThresholdService;
import jakarta.persistence.EntityNotFoundException; // Using standard exception for now
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException; // Using standard exception
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceThresholdServiceImpl implements PriceThresholdService {

    private final PriceThresholdRepository priceThresholdRepository;
    private final UserRepository userRepository;
    private final FlightRepository flightRepository;
    private final HotelRepository hotelRepository;
    private final PriceThresholdMapper priceThresholdMapper; // Inject mapper

    @Override
    @Transactional
    public PriceThresholdDTO createThreshold(CreatePriceThresholdRequest request, String username) {
        log.info("Creating price threshold for user: {}", username);

        // --- Validation ---
        if ((request.getFlightId() == null && request.getHotelId() == null) ||
            (request.getFlightId() != null && request.getHotelId() != null)) {
            throw new IllegalArgumentException("Exactly one of flightId or hotelId must be provided.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));

        Flight flight = null;
        if (request.getFlightId() != null) {
            flight = flightRepository.findById(request.getFlightId())
                    .orElseThrow(() -> new EntityNotFoundException("Flight not found with ID: " + request.getFlightId()));
        }

        Hotel hotel = null;
        if (request.getHotelId() != null) {
            hotel = hotelRepository.findById(request.getHotelId())
                    .orElseThrow(() -> new EntityNotFoundException("Hotel not found with ID: " + request.getHotelId()));
        }

        // --- Creation ---
        PriceThreshold threshold = new PriceThreshold();
        threshold.setUser(user);
        threshold.setFlight(flight);
        threshold.setHotel(hotel);
        threshold.setThresholdPrice(request.getThresholdPrice());

        PriceThreshold savedThreshold = priceThresholdRepository.save(threshold);
        log.info("Successfully created price threshold with ID: {}", savedThreshold.getId());

        return priceThresholdMapper.toDto(savedThreshold); // Map entity to DTO
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceThresholdDTO> getThresholdsByUser(String username) {
        log.info("Fetching price thresholds for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));

        List<PriceThreshold> thresholds = priceThresholdRepository.findByUser(user);
        return thresholds.stream()
                .map(priceThresholdMapper::toDto) // Map list of entities to DTOs
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteThreshold(Long thresholdId, String username) {
        log.info("Attempting to delete price threshold with ID: {} for user: {}", thresholdId, username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));

        PriceThreshold threshold = priceThresholdRepository.findById(thresholdId)
                .orElseThrow(() -> new EntityNotFoundException("Price threshold not found with ID: " + thresholdId));

        // --- Authorization Check ---
        if (!Objects.equals(threshold.getUser().getId(), user.getId())) {
            log.warn("User {} attempted to delete threshold {} owned by user {}", username, thresholdId, threshold.getUser().getUsername());
            throw new AccessDeniedException("User does not have permission to delete this threshold.");
        }

        priceThresholdRepository.delete(threshold);
        log.info("Successfully deleted price threshold with ID: {}", thresholdId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findUsersToNotifyForFlight(Flight flight, BigDecimal newPrice) {
        log.debug("Finding users to notify for flight ID: {} with price <= {}", flight.getId(), newPrice);
        List<PriceThreshold> matchingThresholds = priceThresholdRepository.findByFlightAndThresholdPriceLessThanEqual(flight, newPrice);

        // Fetch users and initialize necessary fields within the transaction
        List<User> users = matchingThresholds.stream()
                .map(PriceThreshold::getUser)
                .distinct() // Ensure each user is included only once
                .peek(user -> { // Initialize required fields to prevent LazyInitializationException
                    user.getUsername(); // Access username
                    user.getEmail();    // Access email
                })
                .collect(Collectors.toList());
        log.debug("Found {} distinct users to notify.", users.size());
        return users;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findUsersToNotifyForHotel(Hotel hotel, BigDecimal newPrice) {
        log.debug("Finding users to notify for hotel ID: {} with price <= {}", hotel.getId(), newPrice);
        List<PriceThreshold> matchingThresholds = priceThresholdRepository.findByHotelAndThresholdPriceLessThanEqual(hotel, newPrice);

        return matchingThresholds.stream()
                .map(PriceThreshold::getUser)
                .distinct() // Ensure each user is included only once
                .collect(Collectors.toList());
    }
}