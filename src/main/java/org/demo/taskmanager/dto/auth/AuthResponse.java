package org.demo.taskmanager.dto.auth;

public record AuthResponse(String token, String username, String role) {
}
