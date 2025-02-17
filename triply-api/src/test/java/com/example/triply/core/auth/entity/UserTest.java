package com.example.triply.core.auth.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

public class UserTest {

    @Test
    public void testNoArgsConstructor() {
        User user = new User();
        // Verify that the no-argument constructor initializes all fields to null
        assertNull(user.getId());
        assertNull(user.getUsername());
        assertNull(user.getPassword());
        assertNull(user.getRole());
    }

    @Test
    public void testUserConstructorWithUsername() {
        String username = "john_doe";
        User user = new User(username);
        // Verify that the constructor sets the username while other fields remain null
        assertEquals(username, user.getUsername());
        assertNull(user.getPassword());
        assertNull(user.getRole());
    }

    @Test
    public void testSettersAndGetters() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setPassword("secret");

        // Using Mockito to create a mock Role instance.
        Role role = Mockito.mock(Role.class);
        when(role.getName()).thenReturn("USER");
        user.setRole(role);

        // Verify that getters return the values set via setters.
        assertEquals(1L, user.getId());
        assertEquals("alice", user.getUsername());
        assertEquals("secret", user.getPassword());
        assertNotNull(user.getRole());
        assertEquals("USER", user.getRole().getName());
    }
}
