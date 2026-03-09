package org.demo.taskmanager.service;

import jakarta.persistence.EntityNotFoundException;
import org.demo.taskmanager.dto.task.TaskRequest;
import org.demo.taskmanager.dto.task.TaskResponse;
import org.demo.taskmanager.model.Role;
import org.demo.taskmanager.model.Task;
import org.demo.taskmanager.model.TaskStatus;
import org.demo.taskmanager.model.User;
import org.demo.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires - TaskService")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @InjectMocks
    private TaskService taskService;

    private User owner;
    private User otherUser;
    private Task task;

    @BeforeEach
    void setUp() {
        owner = User.builder().id(1L).username("owner").role(Role.USER).build();
        otherUser = User.builder().id(2L).username("other").role(Role.USER).build();
        task = Task.builder()
                .id(10L)
                .title("Ma tâche")
                .status(TaskStatus.PENDING)
                .user(owner)
                .build();
    }

    @Test
    @DisplayName("getTasksByUser() - Doit retourner une page de tâches pour l'utilisateur")
    void getTasksByUser_shouldReturnPagedTasks() {
        when(taskRepository.findByUserId(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(task)));

        Page<TaskResponse> result = taskService.getTasksByUser(owner, Pageable.unpaged());

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).title()).isEqualTo("Ma tâche");
    }

    @Test
    @DisplayName("createTask() - Doit créer et retourner une tâche")
    void createTask_shouldCreateAndReturnTask() {
        TaskRequest request = new TaskRequest("Nouvelle tâche", "desc", TaskStatus.PENDING, null);
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponse response = taskService.createTask(request, owner);

        assertThat(response.title()).isEqualTo("Ma tâche");
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("deleteTask() - Doit lancer AccessDeniedException si l'utilisateur n'est pas le propriétaire")
    void deleteTask_shouldThrowAccessDenied_whenNotOwner() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.deleteTask(10L, otherUser))
                .isInstanceOf(AccessDeniedException.class);

        verify(taskRepository, never()).delete(any());
    }

    @Test
    @DisplayName("getTaskById() - Doit lancer EntityNotFoundException si la tâche n'existe pas")
    void getTaskById_shouldThrowNotFound_whenTaskDoesNotExist() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(99L, owner))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
