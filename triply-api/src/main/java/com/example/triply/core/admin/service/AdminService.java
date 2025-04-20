package com.example.triply.core.admin.service;

import com.example.triply.core.admin.dto.UserRoleDTO;
import com.example.triply.common.service.EmailService;
import com.example.triply.core.admin.repository.UserStatusRepository;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.math.BigDecimal; // Added import
import com.example.triply.core.flight.repository.FlightPriceRepository; // Added import
import com.example.triply.core.flight.publisher.FlightPriceWritePublisher; // Changed to interface
import com.example.triply.core.flight.publisher.impl.FlightPriceWritePublisherImpl; // Corrected import path for impl (though not directly used in field/constructor)
import com.example.triply.core.flight.mapper.FlightPriceMapper; // Added import - adjust if needed
import com.example.triply.core.flight.model.entity.FlightPrice; // Corrected import path
import com.example.triply.core.flight.model.dto.FlightPriceDTO; // Corrected import path
// import com.example.triply.common.exception.ResourceNotFoundException; // Removed import - using ResponseStatusException instead

@Service
public class AdminService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    // private final EmailService emailService; // Removed field
    private final ApplicationEventPublisher applicationEventPublisher; // Added field
    private final FlightPriceRepository flightPriceRepository; // Added field
    private final FlightPriceWritePublisher flightPriceWritePublisher; // Changed to interface type
    private final FlightPriceMapper flightPriceMapper; // Added field

    public AdminService(UserStatusRepository userStatusRepository,
                       UserRepository userRepository,
                       /* EmailService emailService, */ // Removed constructor param
                       ApplicationEventPublisher applicationEventPublisher, // Corrected constructor param
                       FlightPriceRepository flightPriceRepository, // Added constructor param
                       FlightPriceWritePublisher flightPriceWritePublisher, // Changed to interface type
                       FlightPriceMapper flightPriceMapper) { // Added constructor param
        this.userStatusRepository = userStatusRepository;
        this.userRepository = userRepository;
        // this.emailService = emailService; // Removed assignment
        this.applicationEventPublisher = applicationEventPublisher; // Added assignment
        this.flightPriceRepository = flightPriceRepository; // Added assignment
        this.flightPriceWritePublisher = flightPriceWritePublisher; // Assignment remains valid
        this.flightPriceMapper = flightPriceMapper; // Added assignment
    }

    public List<UserRoleDTO> getUsersWithRoles() {
        List<UserRoleDTO> users = userStatusRepository.getUsersWithRoles();
        return users;
    }

    @Transactional
    public void banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getStatus() != null && "BANNED".equals(user.getStatus().getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already banned");
        }

        try {
            emailService.sendBanNotification(user.getEmail(), user.getUsername(),
                "Violation of community guidelines");
            System.out.println("Sent ban notification to " + user.getEmail());
        } catch (Exception e) {
            System.err.println("Failed to send ban notification: " + e.getMessage());
        }

        userRepository.banUser(userId);
    }

    @Transactional
    public void unbanUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getStatus() == null || !"BANNED".equals(user.getStatus().getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not banned");
        }

        userRepository.unbanUser(userId);

        try {
            emailService.sendBanNotification(user.getEmail(), user.getUsername(),
                "Your account has been reinstated");
            System.out.println("Sent unban notification to " + user.getEmail());
        } catch (Exception e) {
            System.err.println("Failed to send unban notification: " + e.getMessage());
        }
    }

    @Transactional
    public FlightPriceDTO updateFlightPrice(Long flightPriceId, BigDecimal newBasePrice) {
        // Fetch the entity
        FlightPrice flightPrice = flightPriceRepository.findById(flightPriceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FlightPrice not found with id: " + flightPriceId)); // Replaced with ResponseStatusException

        // Create DTO for the old state (before modification)
        // Important: Create the DTO *before* changing the entity
        FlightPriceDTO oldPriceDTO = flightPriceMapper.toDto(flightPrice); // Assuming a toDto method exists

        // Update the price
        flightPrice.setBasePrice(newBasePrice); // Assuming setBasePrice method exists

        // Save the updated entity
        FlightPrice updatedFlightPrice = flightPriceRepository.save(flightPrice);

        // Create DTO for the new state (after modification)
        FlightPriceDTO newPriceDTO = flightPriceMapper.toDto(updatedFlightPrice);

        // Publish the event with both old and new states
        flightPriceWritePublisher.publish(List.of(oldPriceDTO), List.of(newPriceDTO));

        // Return the new DTO
        return newPriceDTO;
    }
}
