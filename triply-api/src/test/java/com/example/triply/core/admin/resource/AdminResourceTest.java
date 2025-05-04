package com.example.triply.core.admin.resource;

import com.example.triply.core.admin.dto.UserRoleDTO;
import com.example.triply.core.admin.service.AdminService;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminResourceTest {

    private MockMvc mockMvc;
    private AdminService adminService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        adminService = mock(AdminService.class);
        userRepository = mock(UserRepository.class);

        AdminResource adminResource = new AdminResource(userRepository, adminService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(adminResource)
                .addPlaceholderValue("triply.api-version", "v1")
                .build();
    }

    @Test
    void testPerformAction_shouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/admin/promote/42"))
                .andExpect(status().isOk())
                .andExpect(content().string("User promoted to admin successfully"));

        verify(adminService).performUserAction(42L, "promote");
    }

    @Test
    void testGetUsersWithRoles_shouldReturnList() throws Exception {
        UserRoleDTO dto = new UserRoleDTO();
        dto.setUsername("admin");
        dto.setRoleName("ADMIN");

        when(adminService.getUsersWithRoles()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[0].roleName").value("ADMIN"));
    }

    // /admins
    @Test
    void testGetAdmins_shouldReturnList() throws Exception {
        UserRoleDTO dto = new UserRoleDTO();
        dto.setUsername("admin");
        when(userRepository.getUsersAdminRoles()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/admin/admins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("admin"));
    }

    // /isBanned with username
    @Test
    void testIsBanned_withUsername_shouldCallService() throws Exception {
        UserRoleDTO dto = new UserRoleDTO();
        dto.setUsername("bannedUser");

        when(adminService.searchBannedUsersByUsername("john")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/admin/isBanned?username=john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("bannedUser"));
    }

    // /isBanned without username
    @Test
    void testIsBanned_withoutUsername_shouldCallRepository() throws Exception {
        UserRoleDTO dto = new UserRoleDTO();
        dto.setUsername("bannedUser");

        when(userRepository.getBannedUsers()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/admin/isBanned"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("bannedUser"));
    }

    // /ban - success
    @Test
    void testBanUser_success() throws Exception {
        mockMvc.perform(post("/api/v1/admin/ban/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User banned successfully"));

        verify(adminService).banUser(1L);
    }

    // /ban - ResponseStatusException
    @Test
    void testBanUser_responseStatusException() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"))
                .when(adminService).banUser(1L);

        mockMvc.perform(post("/api/v1/admin/ban/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    // /ban - generic exception
    @Test
    void testBanUser_genericException() throws Exception {
        doThrow(new RuntimeException("Unexpected error"))
                .when(adminService).banUser(1L);

        mockMvc.perform(post("/api/v1/admin/ban/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An unexpected error occurred"));
    }

    // /unban - success
    @Test
    void testUnbanUser_success() throws Exception {
        mockMvc.perform(post("/api/v1/admin/unban/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User unbanned successfully"));

        verify(adminService).unbanUser(1L);
    }

    // /unban - ResponseStatusException
    @Test
    void testUnbanUser_responseStatusException() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"))
                .when(adminService).unbanUser(1L);

        mockMvc.perform(post("/api/v1/admin/unban/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    // /unban - generic exception
    @Test
    void testUnbanUser_genericException() throws Exception {
        doThrow(new RuntimeException("Unexpected"))
                .when(adminService).unbanUser(1L);

        mockMvc.perform(post("/api/v1/admin/unban/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("An unexpected error occurred while unbanning the user"));
    }

    @Test
    void testGetCurrentUser_asTriplyUser_shouldReturnUsernameAndId() throws Exception {
        User user = new User();
        user.setId(123L);
        user.setUsername("triplyUser");

        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.isAuthenticated()).thenReturn(true);
        when(mockAuth.getPrincipal()).thenReturn(user);

        SecurityContextHolder.getContext().setAuthentication(mockAuth);

        mockMvc.perform(get("/api/v1/admin/currentuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("triplyUser"))
                .andExpect(jsonPath("$.userId").value("123"));
    }

    @Test
    void testGetCurrentUser_asUserDetails_shouldQueryUserRepository() throws Exception {
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.isAuthenticated()).thenReturn(true);
        when(mockAuth.getPrincipal()).thenReturn("john");
        when(mockAuth.getName()).thenReturn("john");

        User userFromDb = new User();
        userFromDb.setId(456L);
        userFromDb.setUsername("john");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(userFromDb));

        SecurityContextHolder.getContext().setAuthentication(mockAuth);

        mockMvc.perform(get("/api/v1/admin/currentuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"))
                .andExpect(jsonPath("$.userId").value("456"));
    }

    @Test
    void testGetCurrentUser_userNotAuthenticated_shouldReturn401() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(null);

        mockMvc.perform(get("/api/v1/admin/currentuser"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("User is not authenticated"));
    }

    @Test
    void testGetCurrentUser_userNotFound_shouldReturn401() throws Exception {
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.isAuthenticated()).thenReturn(true);
        when(mockAuth.getPrincipal()).thenReturn("ghost");
        when(mockAuth.getName()).thenReturn("ghost");

        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        SecurityContextHolder.getContext().setAuthentication(mockAuth);

        mockMvc.perform(get("/api/v1/admin/currentuser"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("User not found"));
    }

    @Test
    void testSearchUsers_shouldReturnMatchingUsers() throws Exception {
        UserRoleDTO dto = new UserRoleDTO();
        dto.setUsername("admin");

        when(adminService.searchUsersByUsername("admin")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/admin/users/search?username=admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("admin"));
    }

    @Test
    void testPromoteUser_shouldInvokeServiceAndReturnMessage() throws Exception {
        mockMvc.perform(post("/api/v1/admin/promote/77"))
                .andExpect(status().isOk())
                .andExpect(content().string("User promoted to admin successfully"));

        verify(adminService).performUserAction(77L, "promote");
    }

    @Test
    void testDemoteUser_shouldInvokeServiceAndReturnMessage() throws Exception {
        mockMvc.perform(post("/api/v1/admin/demote/88"))
                .andExpect(status().isOk())
                .andExpect(content().string("User demoted from admin successfully"));

        verify(adminService).performUserAction(88L, "demote");
    }

    @Test
    void testGetUser_shouldReturnUserMap() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("username", "alice");
        mockResponse.put("email", "alice@example.com");

    }
}
