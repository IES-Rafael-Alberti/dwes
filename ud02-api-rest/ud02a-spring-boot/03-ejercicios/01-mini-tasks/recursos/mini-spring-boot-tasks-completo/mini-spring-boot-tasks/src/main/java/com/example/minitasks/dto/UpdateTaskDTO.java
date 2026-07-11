package com.example.minitasks.dto;

import jakarta.validation.constraints.Size;

public record UpdateTaskDTO(
        @Size(min = 1, message = "title cannot be blank")
        String title,
        Boolean done
) {}
