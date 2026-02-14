package com.bryan.TodoListAPI.model.dto;

public record TodoResponseDto (
        Long id,
        String title,
        String description
){
}
