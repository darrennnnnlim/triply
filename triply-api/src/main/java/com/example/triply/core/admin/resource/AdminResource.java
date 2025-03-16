package com.example.triply.core.admin.resource;

import com.example.triply.core.admin.dto.UserRoleDTO;
import com.example.triply.core.admin.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@RestController
@RequestMapping("/api/${triply.api-version}/admin")
public class AdminResource {

    private final AdminService adminService;

    // Constructor Injection (Recommended)
    public AdminResource(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserRoleDTO>> getUsersWithRoles() {
        List<UserRoleDTO> usersWithRoles = adminService.getUsersWithRoles();
        return ResponseEntity.ok(usersWithRoles);
    }

    @GetMapping("/admins")
    public ResponseEntity<List<UserRoleDTO>> getAdmins() {
        List<UserRoleDTO> admins = adminService.getUsersAdminRoles();
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/currentuser")
    public ResponseEntity<String> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        String username;

        if (principal instanceof com.example.triply.core.auth.entity.User user) {
            username = user.getUsername();
        } else {
            username = authentication.getName();
        }

        return ResponseEntity.ok(username);
    }

    @PostMapping("/unban/{userId}")
    public ResponseEntity<String> unbanUser(@PathVariable Long userId) {
        try {
            adminService.unbanUser(userId);
            return ResponseEntity.ok("User unbanned successfully");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error unbanning user: " + e.getMessage());
        }
    }

    @PostMapping("/ban/{userId}")
    public ResponseEntity<String> banUser(@PathVariable Long userId) {
        try {
            adminService.banUser(userId);
            return ResponseEntity.ok("User banned successfully");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error banning user: " + e.getMessage());
        }
    }


    @GetMapping("/isBanned")
    public ResponseEntity<List<UserRoleDTO>> getBannedUsers() {
        List<UserRoleDTO> bannedUsers = adminService.getBannedUsers();
        return ResponseEntity.ok(bannedUsers);
    }


}

