package com.win777.backend.service;

import com.win777.backend.dto.LoginRequest;
import com.win777.backend.dto.RegisterRequest;
import com.win777.backend.dto.AuthResponse;
import com.win777.backend.entity.User;
import com.win777.backend.repository.UserRepository;
import com.win777.backend.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setMobile("1234567890");
        registerRequest.setPassword("password123");
        registerRequest.setDeviceFingerprint("device-123");

        loginRequest = new LoginRequest();
        loginRequest.setMobile("1234567890");
        loginRequest.setPassword("password123");

        user = User.builder()
                .id(1L)
                .mobile("1234567890")
                .passwordHash("hashedPassword")
                .deviceFingerprint("device-123")
                .status("ACTIVE")
                .build();
    }

    @Test
    void testRegister_Success() {
        when(userRepository.existsByMobile(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtil.generateToken(anyLong(), anyString())).thenReturn("jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("1234567890", response.getMobile());
        assertEquals(1L, response.getUserId());

        verify(userRepository, times(1)).existsByMobile("1234567890");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegister_MobileAlreadyExists() {
        when(userRepository.existsByMobile(anyString())).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authService.register(registerRequest);
        });

        assertEquals("Mobile number already registered", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLogin_Success() {
        when(userRepository.findByMobile(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.generateToken(anyLong(), anyString())).thenReturn("jwt-token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("1234567890", response.getMobile());
        assertEquals(1L, response.getUserId());

        verify(userRepository, times(1)).findByMobile("1234567890");
    }

    @Test
    void testLogin_InvalidCredentials() {
        when(userRepository.findByMobile(anyString())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void testLogin_WrongPassword() {
        when(userRepository.findByMobile(anyString())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequest);
        });

        assertEquals("Invalid credentials", exception.getMessage());
    }
}
