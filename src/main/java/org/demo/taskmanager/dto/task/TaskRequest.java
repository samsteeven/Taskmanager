package org.demo.taskmanager.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.demo.taskmanager.model.TaskStatus;

import java.time.LocalDate;

public record TaskRequest(
        @NotBlank(message = "Le titre est obligatoire") @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères") String title,

        String description,

        TaskStatus status,

        LocalDate dueDate) {
}
