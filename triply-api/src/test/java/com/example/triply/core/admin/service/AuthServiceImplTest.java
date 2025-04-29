package com.example.triply.core.admin.service;

import com.example.triply.core.admin.entity.UserStatus;
import com.example.triply.core.admin.repository.UserStatusRepository;
import com.example.triply.core.auth.entity.Role;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.auth.repository.RoleRepository;
import com.example.triply.core.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private UserStatusRepository userStatusRepository;
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        userStatusRepository = mock(UserStatusRepository.class);
        adminService = new AdminService(userStatusRepository, userRepository, roleRepository);
        adminService.initUserActions();
    }

    @Test
    void banUser_Success() {
        User user = new User();
        user.setId(1L);
        UserStatus status = new UserStatus();
        status.setStatus("ACTIVE");
        user.setStatus(status);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        adminService.banUser(1L);

        verify(userRepository).banUser(1L);
    }

    @Test
    void banUser_AlreadyBanned() {
        User user = new User();
        user.setId(2L);
        UserStatus status = new UserStatus();
        status.setStatus("BANNED");
        user.setStatus(status);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                adminService.banUser(2L));
        assertEquals("User is already banned", ex.getReason());
    }

    @Test
    void unbanUser_Success() {
        User user = new User();
        user.setId(3L);
        UserStatus status = new UserStatus();
        status.setStatus("BANNED");
        user.setStatus(status);

        when(userRepository.findById(3L)).thenReturn(Optional.of(user));

        adminService.unbanUser(3L);

        verify(userRepository).unbanUser(3L);
    }

    @Test
    void unbanUser_NotBanned() {
        User user = new User();
        user.setId(4L);
        UserStatus status = new UserStatus();
        status.setStatus("ACTIVE");
        user.setStatus(status);

        when(userRepository.findById(4L)).thenReturn(Optional.of(user));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                adminService.unbanUser(4L));
        assertEquals("User is not banned", ex.getReason());
    }

    @Test
    void promoteUser_Success() {
        User user = new User();
        user.setId(5L);
        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        user.setRole(userRole);

        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");

        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));

        adminService.performUserAction(5L, "promote");

        assertEquals("ROLE_ADMIN", user.getRole().getName());
        verify(userRepository).save(user);
    }

    @Test
    void promoteUser_AlreadyAdmin() {
        User user = new User();
        user.setId(6L);
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        user.setRole(adminRole);

        when(userRepository.findById(6L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                adminService.performUserAction(6L, "promote"));
        assertEquals("User is already an admin", ex.getReason());
    }

    @Test
    void demoteUser_Success() {
        User user = new User();
        user.setId(7L);
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        user.setRole(adminRole);

        Role userRole = new Role();
        userRole.setName("ROLE_USER");

        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));

        adminService.performUserAction(7L, "demote");

        assertEquals("ROLE_USER", user.getRole().getName());
        verify(userRepository).save(user);
    }

    @Test
    void demoteUser_NotAdmin() {
        User user = new User();
        user.setId(8L);
        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        user.setRole(userRole);

        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");

        when(userRepository.findById(8L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                adminService.performUserAction(8L, "demote"));
        assertEquals("User is not an admin", ex.getReason());
    }
}
