package org.demo.taskmanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.demo.taskmanager.dto.auth.AuthResponse;
import org.demo.taskmanager.dto.auth.LoginRequest;
import org.demo.taskmanager.dto.auth.RegisterRequest;
import org.demo.taskmanager.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Gestion de l'inscription et la connexion des utilisateurs")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Inscrire un nouvel utilisateur", description = "Crée un nouveau compte utilisateur et retourne un token JWT valide.")
    @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès")
    @ApiResponse(responseCode = "400", description = "Données invalides ou email déjà utilisé")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @Operation(summary = "Connecter un utilisateur", description = "Authentifie un utilisateur avec son email et mot de passe, et retourne un token JWT.")
    @ApiResponse(responseCode = "200", description = "Authentification réussie")
    @ApiResponse(responseCode = "401", description = "Identifiants incorrects")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
