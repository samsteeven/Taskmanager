package org.demo.taskmanager.service;

import lombok.RequiredArgsConstructor;
import org.demo.taskmanager.dto.auth.AuthResponse;
import org.demo.taskmanager.dto.auth.LoginRequest;
import org.demo.taskmanager.dto.auth.RegisterRequest;
import org.demo.taskmanager.model.Role;
import org.demo.taskmanager.model.User;
import org.demo.taskmanager.repository.UserRepository;
import org.demo.taskmanager.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service gérant l'inscription et la connexion des utilisateurs.
 * Applique le principe SRP (Single Responsibility Principle) en n'ayant
 * que la logique d'authentification.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Ce nom d'utilisateur est déjà pris.");
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
        String token = jwtUtils.generateToken(user);
        return new AuthResponse(token, user.getUsername(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalStateException("Utilisateur introuvable après authentification."));

        String token = jwtUtils.generateToken(user);
        return new AuthResponse(token, user.getUsername(), user.getRole().name());
    }
}
