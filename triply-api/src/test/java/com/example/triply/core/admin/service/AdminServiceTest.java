package com.example.triply.core.admin.service;

import com.example.triply.core.admin.dto.UserRoleDTO;
import com.example.triply.core.admin.entity.UserStatus;
import com.example.triply.core.admin.repository.UserStatusRepository;
import com.example.triply.core.auth.entity.Role;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.auth.notification.*;
import com.example.triply.core.auth.repository.RoleRepository;
import com.example.triply.core.auth.repository.UserRepository;
import com.example.triply.core.flight.mapper.FlightPriceMapper;
import com.example.triply.core.flight.model.dto.FlightPriceDTO;
import com.example.triply.core.flight.model.entity.FlightPrice;
import com.example.triply.core.flight.repository.FlightPriceRepository;
import com.example.triply.core.pricing.flight.notification.FlightPriceWritePublisherImpl;
import com.example.triply.core.ratings.service.RatingService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock private UserStatusRepository userStatusRepository;
    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private UserBanWritePublisher userBanWritePublisher;
    @Mock private UserUnbanWritePublisher userUnbanWritePublisher;
    @Mock private FlightPriceRepository flightPriceRepository;
    @Mock private FlightPriceWritePublisherImpl flightPriceWritePublisher;
    @Mock private FlightPriceMapper flightPriceMapper;
    @Mock private RatingService ratingService;
    @Mock private EntityManager entityManager;

    private final User user = new User();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user.setId(1L);
        user.setUsername("testuser");

        // Inject entityManager manually since it's not injected by constructor
        ReflectionTestUtils.setField(adminService, "entityManager", entityManager);
    }

    @Test
    void performUserAction_ban_success() {
        user.setStatus(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        adminService.performUserAction(1L, "ban");

        verify(userRepository).banUser(1L);
    }

    @Test
    void performUserAction_ban_alreadyBanned() {
        UserStatus status = new UserStatus();
        status.setStatus("BANNED");
        user.setStatus(status);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> adminService.performUserAction(1L, "ban"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User is already banned");
    }

    @Test
    void performUserAction_unban_success() {
        UserStatus status = new UserStatus();
        status.setStatus("BANNED");
        user.setStatus(status);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        adminService.performUserAction(1L, "unban");

        verify(userRepository).unbanUser(1L);
    }

    @Test
    void performUserAction_unban_notBanned() {
        user.setStatus(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> adminService.performUserAction(1L, "unban"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User is not banned");
    }

    @Test
    void performUserAction_promote_success() {
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));

        adminService.performUserAction(1L, "promote");

        verify(userRepository).save(user);
    }

    @Test
    void performUserAction_promote_alreadyAdmin() {
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        user.setRole(adminRole);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));

        assertThatThrownBy(() -> adminService.performUserAction(1L, "promote"))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void performUserAction_demote_success() {
        Role adminRole = new Role(); adminRole.setName("ROLE_ADMIN");
        Role userRole = new Role(); userRole.setName("ROLE_USER");
        user.setRole(adminRole);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));

        adminService.performUserAction(1L, "demote");

        verify(userRepository).save(user);
    }

    @Test
    void performUserAction_unknownAction_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> adminService.performUserAction(1L, "unknown"))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void banUser_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        adminService.banUser(1L);

        verify(ratingService).softDeleteAllBy(1L);
        verify(userBanWritePublisher).publish(any(UserBanWriteEvent.class));
        verify(userRepository).banUser(1L);
    }

    @Test
    void banUser_alreadyBanned() {
        UserStatus status = new UserStatus();
        status.setStatus("BANNED");
        user.setStatus(status);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> adminService.banUser(1L))
                .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void unbanUser_success() {
        UserStatus status = new UserStatus();
        status.setStatus("BANNED");
        user.setStatus(status);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        adminService.unbanUser(1L);

        verify(userRepository).unbanUser(1L);
        verify(userUnbanWritePublisher).publish(any(UserUnbanWriteEvent.class));
        verify(ratingService).undoSoftDeleteAllBy(1L);
    }

    @Test
    void updateFlightPrice_success() {
        FlightPrice price = new FlightPrice();
        price.setId(1L);

        FlightPriceDTO dto = new FlightPriceDTO();

        when(flightPriceRepository.findById(1L)).thenReturn(Optional.of(price));
        when(flightPriceMapper.toDto((FlightPrice) any())).thenReturn(dto);
        when(flightPriceRepository.save(price)).thenReturn(price);

        FlightPriceDTO result = adminService.updateFlightPrice(1L, BigDecimal.TEN);
        assertThat(result).isNotNull();
        verify(flightPriceWritePublisher).publish(any(), any());
    }

    @Test
    void getUsersWithRoles_success() {
        when(userStatusRepository.getUsersWithRoles()).thenReturn(List.of(new UserRoleDTO()));
        assertThat(adminService.getUsersWithRoles()).hasSize(1);
    }

    @Test
    void searchUsersByUsername_blank_returnsAll() {
        when(userStatusRepository.getUsersWithRoles()).thenReturn(List.of(new UserRoleDTO()));
        assertThat(adminService.searchUsersByUsername("")).hasSize(1);
    }

    @Test
    void searchUsersByUsername_keyword_returnsFiltered() {
        when(userStatusRepository.searchUsersByUsername("john")).thenReturn(List.of(new UserRoleDTO()));
        assertThat(adminService.searchUsersByUsername("john")).hasSize(1);
    }

    @Test
    void searchBannedUsersByUsername_returns() {
        when(userStatusRepository.searchBannedUsersByUsername("john")).thenReturn(List.of(new UserRoleDTO()));
        assertThat(adminService.searchBannedUsersByUsername("john")).hasSize(1);
    }

    @Test
    void promoteToAdmin_success() {
        Role admin = new Role(); admin.setName("ROLE_ADMIN");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(admin));

        adminService.promoteToAdmin(1L);
        verify(userRepository).save(user);
        verify(entityManager).flush();
    }

    @Test
    void demoteToUser_success() {
        Role userRole = new Role(); userRole.setName("ROLE_USER");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));

        adminService.demoteToUser(1L);
        verify(userRepository).save(user);
        verify(entityManager).flush();
    }

    @Test
    void getUserById_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Map<String, Object> result = adminService.getUserById(1L);
        assertThat(result.get("id")).isEqualTo(1L);
        assertThat(result.get("username")).isEqualTo("testuser");
    }
}
