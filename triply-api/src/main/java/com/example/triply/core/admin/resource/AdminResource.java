package com.example.triply.core.admin.resource;

import com.example.triply.core.admin.dto.UserRoleDTO;
import com.example.triply.core.admin.service.AdminService;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.auth.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/${triply.api-version}/admin")
public class AdminResource {

    private final UserRepository userRepository;
    private final AdminService adminService;
    private static final String UNEXPECTED_ERROR_MESSAGE = "An unexpected error occurred";

    public AdminResource(UserRepository userRepository, AdminService adminService) {
        this.userRepository = userRepository;
        this.adminService = adminService;
    }

    @PostMapping("/{action}/{userId}")
    public ResponseEntity<String> performAction(@PathVariable String action, @PathVariable Long userId) {
        adminService.performUserAction(userId, action);
        return ResponseEntity.ok("Action performed: " + action);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserRoleDTO>> getUsersWithRoles() {
        List<UserRoleDTO> usersWithRoles = adminService.getUsersWithRoles();
        return ResponseEntity.ok(usersWithRoles);
    }

    @GetMapping("/admins")
    public ResponseEntity<List<UserRoleDTO>> getAdmins() {
        List<UserRoleDTO> admins = userRepository.getUsersAdminRoles();
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/isBanned")
    public ResponseEntity<List<UserRoleDTO>> getBannedUsers(@RequestParam(required = false) String username) {
        if (username != null && !username.isEmpty()) {
            List<UserRoleDTO> users = adminService.searchBannedUsersByUsername(username);
            return ResponseEntity.ok(users);
        }
        List<UserRoleDTO> bannedUsers = userRepository.getBannedUsers();
        return ResponseEntity.ok(bannedUsers);
    }

    @PostMapping("/ban/{userId}")
    public ResponseEntity<String> banUser(@PathVariable Long userId) {
        try {
            adminService.banUser(userId);
            return ResponseEntity.ok("User banned successfully");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(UNEXPECTED_ERROR_MESSAGE);
        }
    }

    @PostMapping("/unban/{userId}")
    public ResponseEntity<String> unbanUser(@PathVariable Long userId) {
        try {
            adminService.unbanUser(userId);
            return ResponseEntity.ok("User unbanned successfully");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred while unbanning the user");
        }
    }

    @GetMapping("/currentuser")
    public ResponseEntity<Object> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        String username;
        Long id;

        if (principal instanceof com.example.triply.core.auth.entity.User user) {
            username = user.getUsername();
            id = user.getId();
        } else {
            username = authentication.getName();
            Optional<User> userFromDB = userRepository.findByUsername(username);

            if (userFromDB.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            User user = userFromDB.get();
            id = user.getId();
        }
        Map<String, Object> response = new HashMap<>();
        response.put("username", username);
        response.put("userId", id.toString());

        return ResponseEntity.ok(response);

    }

    @GetMapping("/users/search")
    public ResponseEntity<List<UserRoleDTO>> searchUsers(@RequestParam String username) {
        List<UserRoleDTO> users = adminService.searchUsersByUsername(username);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/promote/{userId}")
    public ResponseEntity<String> promoteUser(@PathVariable Long userId) {
        adminService.performUserAction(userId, "promote");
        return ResponseEntity.ok("User promoted to admin successfully");
    }

    @PostMapping("/demote/{userId}")
    public ResponseEntity<String> demoteUser(@PathVariable Long userId) {
        adminService.performUserAction(userId, "demote");
        return ResponseEntity.ok("User demoted from admin successfully");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getUserById(userId));
    }

}