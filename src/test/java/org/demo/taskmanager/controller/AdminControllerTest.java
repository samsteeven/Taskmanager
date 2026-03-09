package org.demo.taskmanager.controller;

import org.demo.taskmanager.model.Role;
import org.demo.taskmanager.service.UserService;
import org.demo.taskmanager.repository.UserRepository;
import org.demo.taskmanager.security.JwtAuthFilter;
import org.demo.taskmanager.security.JwtUtils;
import org.demo.taskmanager.security.SecurityBeansConfig;
import org.demo.taskmanager.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(AdminController.class)
@Import({ SecurityConfig.class, SecurityBeansConfig.class, JwtAuthFilter.class, JwtUtils.class })
@DisplayName("Tests d'intégration - AdminController")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("GET /api/v1/admin/users - Doit retourner la liste des utilisateurs pour un admin")
    void getAllUsers_shouldReturnList_whenAdmin() throws Exception {
        org.demo.taskmanager.model.User user = org.demo.taskmanager.model.User.builder()
                .id(1L)
                .username("user1")
                .email("user1@demo.com")
                .role(Role.USER)
                .build();

        when(userService.getAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[0].email").value("user1@demo.com"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    @DisplayName("GET /api/v1/admin/users - Doit retourner 403 pour un simple utilisateur")
    void getAllUsers_shouldReturn403_whenNotAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isForbidden());
    }
}
