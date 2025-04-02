package com.example.triply.core.auth.entity;

import com.example.triply.core.admin.entity.UserStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

public class UserTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("secret");

        UserStatus status = new UserStatus();
        user.setStatus(status);

        Role role1 = new Role();
        Role role2 = new Role();
        Set<Role> roles = new HashSet<>();
        roles.add(role1);
        roles.add(role2);
        user.setRoles(roles);

        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("secret", user.getPassword());
        assertEquals(status, user.getStatus());
        assertEquals(2, user.getRoles().size());
    }

    @Test
    void testUsernameConstructor() {
        User user = new User("john_doe");
        assertEquals("john_doe", user.getUsername());
    }
}
