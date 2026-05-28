package com.example.cloud.storage.controller;

import com.example.cloud.storage.LoginRequest;
import com.example.cloud.storage.exception.InvalidPasswordException;
import com.example.cloud.storage.exception.UserAlreadyExistsException;
import com.example.cloud.storage.exception.UserNotFoundException;
import com.example.cloud.storage.model.User;
import com.example.cloud.storage.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setLogin("testuser");
        loginRequest.setPassword("password123");

        user = new User("testuser", "password123");
        user.setId(1L);
    }

    @Test
    void login_Success() {
        when(userRepository.findByLogin("testuser")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        Map<String, String> response = authController.login(loginRequest);

        assertNotNull(response);
        assertTrue(response.containsKey("auth-token"));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void login_UserNotFound_ThrowsException() {
        when(userRepository.findByLogin("testuser")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authController.login(loginRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_InvalidPassword_ThrowsException() {
        when(userRepository.findByLogin("testuser")).thenReturn(Optional.of(user));
        loginRequest.setPassword("wrongpassword");

        assertThrows(InvalidPasswordException.class, () -> authController.login(loginRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_Success() {
        when(userRepository.findByLogin("testuser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        Map<String, String> response = authController.register(loginRequest);

        assertNotNull(response);
        assertEquals("Пользователь успешно зарегистрирован", response.get("message"));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_UserAlreadyExists_ThrowsException() {
        when(userRepository.findByLogin("testuser")).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> authController.register(loginRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void logout_Success() {
        String token = "test-token";
        user.setToken(token);
        when(userRepository.findByToken(token)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        Map<String, String> response = authController.logout(token);

        assertNotNull(response);
        assertEquals("Выход выполнен успешно", response.get("message"));
        assertNull(user.getToken());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void logout_InvalidToken_ThrowsException() {
        String token = "invalid-token";
        when(userRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authController.logout(token));
        verify(userRepository, never()).save(any(User.class));
    }
}