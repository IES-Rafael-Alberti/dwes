package com.example.tasks.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTaskDTO(@NotBlank(message = "El título no puede estar vacío")
                            String title) {
}
