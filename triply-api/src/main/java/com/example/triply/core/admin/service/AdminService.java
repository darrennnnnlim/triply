package com.example.triply.core.admin.service;

import com.example.triply.core.admin.dto.UserRoleDTO;
// import com.example.triply.common.service.EmailService; // Removed import
import com.example.triply.core.admin.repository.UserStatusRepository;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.auth.event.UserBannedEvent; // Added import
import com.example.triply.core.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher; // Added import
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AdminService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    // private final EmailService emailService; // Removed field
    private final ApplicationEventPublisher applicationEventPublisher; // Added field

    public AdminService(UserStatusRepository userStatusRepository,
                       UserRepository userRepository,
                       /* EmailService emailService, */ // Removed constructor param
                       ApplicationEventPublisher applicationEventPublisher) { // Added constructor param
        this.userStatusRepository = userStatusRepository;
        this.userRepository = userRepository;
        // this.emailService = emailService; // Removed assignment
        this.applicationEventPublisher = applicationEventPublisher; // Added assignment
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

        // Publish user banned event
        String banReason = "Violation of community guidelines"; // Default reason
        applicationEventPublisher.publishEvent(new UserBannedEvent(this, user.getEmail(), user.getUsername(), banReason));

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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getStatus() == null || !"BANNED".equals(user.getStatus().getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not banned");
        }

        userRepository.unbanUser(userId);

        // Removed direct email sending block for unban as well
        // try {
        //     emailService.sendBanNotification(user.getEmail(), user.getUsername(),
        //         "Your account has been reinstated");
        //     System.out.println("Sent unban notification to " + user.getEmail());
        // } catch (Exception e) {
        //     System.err.println("Failed to send unban notification: " + e.getMessage());
        // }
    }
}
