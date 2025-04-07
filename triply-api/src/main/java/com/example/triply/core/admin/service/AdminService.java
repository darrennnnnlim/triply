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

@Service
public class AdminService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public AdminService(UserStatusRepository userStatusRepository,
                       UserRepository userRepository,
                       EmailService emailService) {
        this.userStatusRepository = userStatusRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
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


}
