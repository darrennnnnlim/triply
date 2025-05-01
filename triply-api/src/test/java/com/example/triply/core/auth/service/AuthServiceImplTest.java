package com.example.triply.core.auth.service;

import com.example.triply.common.exception.UserNotFoundException;
import com.example.triply.core.auth.entity.User;
import com.example.triply.core.auth.notification.UserRegistrationWritePublisher;
import com.example.triply.core.auth.repository.UserRepository;
import com.example.triply.core.auth.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.example.triply.common.exception.InvalidCurrentPasswordException;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Optional;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthServiceImpl authService;
    private UserRegistrationWritePublisher userRegistrationWritePublisher;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userRegistrationWritePublisher = mock(UserRegistrationWritePublisher.class);
        authService = new AuthServiceImpl(
                userRepository, null, passwordEncoder, null, null, null, null, null, userRegistrationWritePublisher
        );
    }

    @Test
    void resetPassword_Success() {
        User user = new User();
        user.setUsername("alice");
        user.setPassword("hashedOld");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldpass", "hashedOld")).thenReturn(true);
        when(passwordEncoder.encode("newpass")).thenReturn("hashedNew");

        boolean result = authService.resetPassword("alice", "oldpass", "newpass");

        assertTrue(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void resetPassword_CurrentPasswordIncorrect() {
        User user = new User();
        user.setUsername("bob");
        user.setPassword("hashedOld");

        when(userRepository.findByUsername("bob")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "hashedOld")).thenReturn(false);

        assertThrows(InvalidCurrentPasswordException.class, () ->
                authService.resetPassword("bob", "wrongpass", "newpass")
        );
    }

    @Test
    void resetPassword_NewPasswordSameAsCurrent() {
        when(userRepository.findByUsername("bob")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                authService.resetPassword("bob", "pass", "pass")
        );
    }

}
