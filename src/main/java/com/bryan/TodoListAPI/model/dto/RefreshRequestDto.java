package com.bryan.TodoListAPI.model.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequestDto(
        @NotBlank
        String refreshToken
) {
}
