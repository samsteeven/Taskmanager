package org.demo.taskmanager.dto;

import java.time.LocalDateTime;

/**
 * DTO standardisé pour toutes les réponses d'erreur de l'API.
 * Structure uniforme : timestamp, status, error, message, path.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path) {
}
