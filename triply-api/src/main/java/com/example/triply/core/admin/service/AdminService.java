package com.example.triply.core.admin.service;

import com.example.triply.core.admin.dto.UserRoleDTO;
// import com.example.triply.common.service.EmailService; // Removed import
import com.example.triply.core.admin.repository.UserStatusRepository;
import com.example.triply.core.auth.entity.Role;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.auth.repository.RoleRepository;
// import com.example.triply.core.auth.event.UserBannedEvent; // Removed Spring event import
import com.example.triply.core.auth.notification.UserBanWriteEvent; // Added in-house event import
import com.example.triply.core.auth.notification.UserBanWritePublisher; // Added in-house publisher import
import com.example.triply.core.auth.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
// import org.springframework.context.ApplicationEventPublisher; // Removed Spring publisher import
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.math.BigDecimal; // Added import
import com.example.triply.core.flight.repository.FlightPriceRepository; // Added import
import com.example.triply.core.pricing.flight.notification.FlightPriceWritePublisherImpl; // Corrected package path
// Removed import of FlightPriceWritePublisher interface
import com.example.triply.core.flight.mapper.FlightPriceMapper; // Added import - adjust if needed
import com.example.triply.core.flight.model.entity.FlightPrice; // Corrected import path
import com.example.triply.core.flight.model.dto.FlightPriceDTO; // Corrected import path
// import com.example.triply.common.exception.ResourceNotFoundException; // Removed import - using ResponseStatusException instead
import java.util.Map;
import java.util.function.Consumer;

@Service
public class AdminService {

    private static final String STATUS_BANNED = "BANNED";
    private static final String USER_ALREADY_BANNED = "User is already banned";
    private static final String USER_NOT_BANNED = "User is not banned";
    private static final String USER_NOT_FOUND = "User not found";
    private static final String UNKNOWN_ACTION = "Unknown action";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ADMIN_NOT_FOUND = "Admin role not found";

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final Map<String, Consumer<User>> userActions = new HashMap<>();
    // private final ApplicationEventPublisher applicationEventPublisher; // Removed Spring publisher field
    private final UserBanWritePublisher userBanWritePublisher; // Added in-house publisher field
    private final FlightPriceRepository flightPriceRepository; // Added field
    private final FlightPriceWritePublisherImpl flightPriceWritePublisher; // Changed to concrete class type
    private final FlightPriceMapper flightPriceMapper; // Added field

    public AdminService(UserStatusRepository userStatusRepository,
                       UserRepository userRepository,
                        RoleRepository roleRepository,
                       UserBanWritePublisher userBanWritePublisher, // Added in-house publisher
                       FlightPriceRepository flightPriceRepository, // Added constructor param
                       FlightPriceWritePublisherImpl flightPriceWritePublisher, // Use concrete class type
                       FlightPriceMapper flightPriceMapper) { // Added constructor param
        this.userStatusRepository = userStatusRepository;
        this.userRepository = userRepository;
        // this.applicationEventPublisher = applicationEventPublisher; // Removed assignment
        this.userBanWritePublisher = userBanWritePublisher; // Added assignment
        this.flightPriceRepository = flightPriceRepository; // Added assignment
        this.flightPriceWritePublisher = flightPriceWritePublisher;
        this.flightPriceMapper = flightPriceMapper; // Added assignment
        this.roleRepository = roleRepository;
        initUserActions();
    }

    @PostConstruct
     void initUserActions() {
        userActions.put("ban", user -> {
            if (user.getStatus() != null && STATUS_BANNED.equals(user.getStatus().getStatus())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, USER_ALREADY_BANNED);
            }
            userRepository.banUser(user.getId());
        });
        userActions.put("unban", user -> {
            if (user.getStatus() == null || !STATUS_BANNED.equals(user.getStatus().getStatus())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, USER_NOT_BANNED);
            }
            userRepository.unbanUser(user.getId());
        });

        userActions.put("promote", user -> {
            Role adminRole = roleRepository.findByName(ROLE_ADMIN)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ADMIN_NOT_FOUND));
            if (user.getRole() != null && user.getRole().equals(adminRole)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already an admin");
            }
            user.setRole(adminRole);
            userRepository.save(user);
        });

        userActions.put("demote", user -> {
            Role adminRole = roleRepository.findByName(ROLE_ADMIN)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ADMIN_NOT_FOUND));
            if (user.getRole() == null || !user.getRole().equals(adminRole)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not an admin");
            }
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User role not found"));
            user.setRole(userRole);
            userRepository.save(user);
        });

    }

    @Transactional
    public void performUserAction(Long userId, String action) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
        Consumer<User> userAction = userActions.get(action);
        if (userAction == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, UNKNOWN_ACTION);
        }
        userAction.accept(user);
    }

    public List<UserRoleDTO> getUsersWithRoles() {
        return userStatusRepository.getUsersWithRoles();
    }

    @Transactional
    public void banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));

        if (user.getStatus() != null && STATUS_BANNED.equals(user.getStatus().getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, USER_ALREADY_BANNED);
        }

        // Publish user banned event using in-house publisher
        String banReason = "Violation of community guidelines"; // Default reason
        UserBanWriteEvent event = new UserBanWriteEvent(this, user, banReason);
        userBanWritePublisher.publish(event);

        // Removed direct email sending block
        // try {
        //     emailService.sendBanNotification(user.getEmail(), user.getUsername(), banReason);
        //     System.out.println("Sent ban notification to " + user.getEmail());
        // } catch (Exception e) {
        //     System.err.println("Failed to send ban notification: " + e.getMessage());
        // }
        userRepository.banUser(userId);
    }

    @Transactional
    public void unbanUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));

        if (user.getStatus() == null || !STATUS_BANNED.equals(user.getStatus().getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, USER_NOT_BANNED);
        }
        userRepository.unbanUser(userId);

        // Consider adding an unban event/notification if needed
        // Removed direct email sending block for unban as well
        // try {
        //     emailService.sendBanNotification(user.getEmail(), user.getUsername(),
        //         "Your account has been reinstated");
        //     System.out.println("Sent unban notification to " + user.getEmail());
        // } catch (Exception e) {
        //     System.err.println("Failed to send unban notification: " + e.getMessage());
        // }
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
    }    public List<UserRoleDTO> searchUsersByUsername(String username) {
        if (username == null || username.isEmpty()) {
            return userStatusRepository.getUsersWithRoles();
        }
        return userStatusRepository.searchUsersByUsername(username);
    }

    public List<UserRoleDTO> searchBannedUsersByUsername(String username) {
        return userStatusRepository.searchBannedUsersByUsername(username);
    }

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void promoteToAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));

        Role adminRole = roleRepository.findByName(ROLE_ADMIN)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ADMIN_NOT_FOUND));

        user.setRole(adminRole);
        userRepository.save(user);
        entityManager.flush();
    }

    @Transactional
    public void demoteToUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User role not found"));

        user.setRole(userRole);
        userRepository.save(user);
        entityManager.flush();
    }

    @Transactional
    public Map<String, Object> getUserById(Long userId) {
        // Fetch user by ID or throw an exception if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("username", user.getUsername());

        return userMap;
    }

}