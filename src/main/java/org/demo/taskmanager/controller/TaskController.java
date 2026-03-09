package org.demo.taskmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.demo.taskmanager.dto.task.TaskRequest;
import org.demo.taskmanager.dto.task.TaskResponse;
import org.demo.taskmanager.model.User;
import org.demo.taskmanager.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tâches", description = "Gestion du cycle de vie des tâches")
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Récupérer toutes les tâches", description = "Récupère la liste paginée des tâches appartenant à l'utilisateur connecté.")
    @ApiResponse(responseCode = "200", description = "Liste des tâches récupérée avec succès")
    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getAllTasks(
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(taskService.getTasksByUser(currentUser, pageable));
    }

    @Operation(summary = "Récupérer une tâche", description = "Récupère les détails d'une tâche spécifique par son ID.")
    @ApiResponse(responseCode = "200", description = "Tâche trouvée")
    @ApiResponse(responseCode = "404", description = "Tâche non trouvée ou non autorisée")
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(taskService.getTaskById(id, currentUser));
    }

    @Operation(summary = "Créer une tâche", description = "Crée une nouvelle tâche pour l'utilisateur connecté.")
    @ApiResponse(responseCode = "201", description = "Tâche créée avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides")
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(request, currentUser));
    }

    @Operation(summary = "Modifier une tâche", description = "Met à jour les informations d'une tâche existante.")
    @ApiResponse(responseCode = "200", description = "Tâche mise à jour avec succès")
    @ApiResponse(responseCode = "404", description = "Tâche non trouvée ou non autorisée")
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(taskService.updateTask(id, request, currentUser));
    }

    @Operation(summary = "Supprimer une tâche", description = "Supprime une tâche existante.")
    @ApiResponse(responseCode = "204", description = "Tâche supprimée avec succès")
    @ApiResponse(responseCode = "404", description = "Tâche non trouvée ou non autorisée")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser) {
        taskService.deleteTask(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
