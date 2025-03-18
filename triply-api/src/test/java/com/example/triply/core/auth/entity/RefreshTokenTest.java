package com.example.triply.core.auth.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import java.util.Date;

public class RefreshTokenTest {

    @Test
    public void testDefaultConstructor() {
        RefreshToken refreshToken = new RefreshToken();
        // By default, id, token, user, and expiryDate should be null
        assertNull(refreshToken.getId());
        assertNull(refreshToken.getToken());
        assertNull(refreshToken.getUser());
        assertNull(refreshToken.getExpiryDate());
        // The default for a boolean is false
        assertFalse(refreshToken.isRevoked());
    }

    @Test
    public void testSettersAndGetters() {
        RefreshToken refreshToken = new RefreshToken();
        Long expectedId = 1L;
        String expectedToken = "abc123";
        Date expectedExpiryDate = new Date();

        // Set values
        refreshToken.setId(expectedId);
        refreshToken.setToken(expectedToken);
        refreshToken.setExpiryDate(expectedExpiryDate);
        refreshToken.setRevoked(true);

        // Using Mockito to create a mock User instance.
        User mockUser = Mockito.mock(User.class);
        when(mockUser.getUsername()).thenReturn("testUser");
        refreshToken.setUser(mockUser);

        // Assert values are correctly retrieved.
        assertEquals(expectedId, refreshToken.getId());
        assertEquals(expectedToken, refreshToken.getToken());
        assertEquals(expectedExpiryDate, refreshToken.getExpiryDate());
        assertTrue(refreshToken.isRevoked());
        assertNotNull(refreshToken.getUser());
        assertEquals("testUser", refreshToken.getUser().getUsername());
    }
}
