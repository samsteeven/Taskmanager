package org.demo.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.demo.taskmanager.dto.task.TaskRequest;
import org.demo.taskmanager.dto.task.TaskResponse;
import org.demo.taskmanager.model.TaskStatus;
import org.demo.taskmanager.service.TaskService;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(TaskController.class)
@Import({ SecurityConfig.class, SecurityBeansConfig.class, JwtAuthFilter.class, JwtUtils.class })
@DisplayName("Tests d'intégration - TaskController")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("GET /api/v1/tasks - Doit retourner une liste paginée")
    void getAllTasks_shouldReturnPagedTasks() throws Exception {
        TaskResponse response = new TaskResponse(1L, "Titre", "Desc", TaskStatus.PENDING, null, null, null);
        when(taskService.getTasksByUser(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Titre"));
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("POST /api/v1/tasks - Doit créer une tâche")
    void createTask_shouldReturnCreated() throws Exception {
        TaskRequest request = new TaskRequest("Ma tâche", "Description", TaskStatus.PENDING, null);
        TaskResponse response = new TaskResponse(1L, "Ma tâche", "Description", TaskStatus.PENDING, null, null, null);

        when(taskService.createTask(any(TaskRequest.class), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Ma tâche"));
    }

    @Test
    @DisplayName("GET /api/v1/tasks - Doit retourner 401 si non authentifié")
    void getAllTasks_shouldReturn401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isForbidden()); // Spring Security default without custom entry point
    }
}
