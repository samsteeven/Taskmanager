package org.demo.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.demo.taskmanager.dto.auth.RegisterRequest;
import org.demo.taskmanager.dto.auth.AuthResponse;
import org.demo.taskmanager.service.AuthService;
import org.demo.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.demo.taskmanager.security.JwtAuthFilter;
import org.demo.taskmanager.security.JwtUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.demo.taskmanager.security.SecurityBeansConfig;
import org.demo.taskmanager.security.SecurityConfig;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@WebMvcTest(AuthController.class)
@Import({ SecurityConfig.class, SecurityBeansConfig.class, JwtAuthFilter.class, JwtUtils.class })
@DisplayName("Tests d'intégration - AuthController")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;
    @MockitoBean
    private UserRepository userRepository;

    @Test
    @DisplayName("POST /api/v1/auth/register - Doit retourner 201 avec un token valide")
    void register_shouldReturn201_withToken() throws Exception {
        RegisterRequest request = new RegisterRequest("newuser", "password123");
        AuthResponse response = new AuthResponse("mock-token", "newuser", "USER");

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("mock-token"))
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - Doit retourner 400 si le username est vide")
    void register_shouldReturn400_whenUsernameIsBlank() throws Exception {
        RegisterRequest request = new RegisterRequest("", "password123");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.username").exists());
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - Doit retourner 400 si le mot de passe est trop court")
    void register_shouldReturn400_whenPasswordTooShort() throws Exception {
        RegisterRequest request = new RegisterRequest("validuser", "abc");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").exists());
    }
}
