package org.demo.taskmanager.controller;

import lombok.RequiredArgsConstructor;
import org.demo.taskmanager.dto.user.UserResponse;
import org.demo.taskmanager.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur réservé aux Administrateurs.
 * Toutes les routes sont protégées par @PreAuthorize("hasRole('ADMIN')").
 */
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Administration", description = "Endpoints réservés aux administrateurs (Gestion des utilisateurs)")
public class AdminController {

    private final UserService userService;

    @Operation(summary = "Lister tous les utilisateurs", description = "Récupère la liste de tous les utilisateurs inscrits sur la plateforme.")
    @ApiResponse(responseCode = "200", description = "Liste des utilisateurs récupérée avec succès")
    @ApiResponse(responseCode = "403", description = "Accès refusé. Réservé aux administrateurs.")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers().stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Supprimer un utilisateur", description = "Supprime définitivement un compte utilisateur.")
    @ApiResponse(responseCode = "204", description = "Utilisateur supprimé avec succès")
    @ApiResponse(responseCode = "403", description = "Accès refusé.")
    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
