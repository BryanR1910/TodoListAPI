package com.bryan.TodoListAPI.model.dto;

public record RegisterRequestDto(
        String name,
        String email,
        String password
) {
}
