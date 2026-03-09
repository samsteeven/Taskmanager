package org.demo.taskmanager.service;

import org.demo.taskmanager.dto.auth.AuthResponse;
import org.demo.taskmanager.dto.auth.LoginRequest;
import org.demo.taskmanager.dto.auth.RegisterRequest;
import org.demo.taskmanager.model.Role;
import org.demo.taskmanager.model.User;
import org.demo.taskmanager.repository.UserRepository;
import org.demo.taskmanager.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - AuthService")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("encoded-password")
                .role(Role.USER)
                .build();
    }

    @Test
    @DisplayName("register() - Doit créer un utilisateur et retourner un token")
    void register_shouldCreateUserAndReturnToken() {
        RegisterRequest request = new RegisterRequest("testuser", "password123");
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtUtils.generateToken(any(User.class))).thenReturn("mocked-jwt-token");

        AuthResponse response = authService.register(request);

        assertThat(response.token()).isEqualTo("mocked-jwt-token");
        assertThat(response.username()).isEqualTo("testuser");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("register() - Doit lancer une exception si le username existe déjà")
    void register_shouldThrowException_whenUsernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest("testuser", "password123");
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("déjà pris");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("login() - Doit authentifier et retourner un token valide")
    void login_shouldAuthenticateAndReturnToken() {
        LoginRequest request = new LoginRequest("testuser", "password123");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(jwtUtils.generateToken(mockUser)).thenReturn("mocked-jwt-token");

        AuthResponse response = authService.login(request);

        assertThat(response.token()).isEqualTo("mocked-jwt-token");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("login() - Doit lancer une exception pour des mauvaises credentials")
    void login_shouldThrowException_whenBadCredentials() {
        LoginRequest request = new LoginRequest("testuser", "wrongpassword");
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }
}
