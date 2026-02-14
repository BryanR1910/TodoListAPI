package com.bryan.TodoListAPI.model.dto;

import jakarta.validation.constraints.NotBlank;

public record TodoRequestDto(
        @NotBlank
        String title,
        @NotBlank
        String description
) {
}
