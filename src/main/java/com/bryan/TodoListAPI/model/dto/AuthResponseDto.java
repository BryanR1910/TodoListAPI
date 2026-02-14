package com.bryan.TodoListAPI.model.dto;

public record AuthResponseDto (
        String accessToken,
        String refreshToken
){}
