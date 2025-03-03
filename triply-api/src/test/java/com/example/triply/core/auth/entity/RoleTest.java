package com.example.triply.core.auth.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RoleTest {

    @Test
    public void testDefaultConstructorAndSetters() {
        Role role = new Role();
        // By default, both fields should be null
        assertNull(role.getId());
        assertNull(role.getName());

        // Set values using setters
        Long expectedId = 1L;
        String expectedName = "ROLE_USER";
        role.setId(expectedId);
        role.setName(expectedName);

        // Verify that the getters return the expected values
        assertEquals(expectedId, role.getId());
        assertEquals(expectedName, role.getName());
    }
}
