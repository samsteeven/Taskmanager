package org.demo.taskmanager.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.demo.taskmanager.dto.task.TaskRequest;
import org.demo.taskmanager.dto.task.TaskResponse;
import org.demo.taskmanager.model.Task;
import org.demo.taskmanager.model.User;
import org.demo.taskmanager.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service métier pour la gestion des tâches.
 * Assure que chaque utilisateur ne peut accéder/modifier que ses propres
 * tâches.
 */
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksByUser(User currentUser, Pageable pageable) {
        return taskRepository.findByUserId(currentUser.getId(), pageable)
                .map(TaskResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id, User currentUser) {
        Task task = findTaskOrThrow(id);
        checkOwnership(task, currentUser);
        return TaskResponse.fromEntity(task);
    }

    @Transactional
    public TaskResponse createTask(TaskRequest request, User currentUser) {
        Task task = Task.builder()
                .title(request.title())
                .description(request.description())
                .status(request.status() != null ? request.status() : org.demo.taskmanager.model.TaskStatus.PENDING)
                .dueDate(request.dueDate())
                .user(currentUser)
                .build();

        return TaskResponse.fromEntity(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest request, User currentUser) {
        Task task = findTaskOrThrow(id);
        checkOwnership(task, currentUser);

        task.setTitle(request.title());
        task.setDescription(request.description());
        if (request.status() != null)
            task.setStatus(request.status());
        task.setDueDate(request.dueDate());

        return TaskResponse.fromEntity(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long id, User currentUser) {
        Task task = findTaskOrThrow(id);
        checkOwnership(task, currentUser);
        taskRepository.delete(task);
    }

    // --- Méthodes privées utilitaires ---

    private Task findTaskOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tâche non trouvée avec l'id : " + id));
    }

    private void checkOwnership(Task task, User currentUser) {
        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Vous n'avez pas le droit d'accéder à cette tâche.");
        }
    }
}
