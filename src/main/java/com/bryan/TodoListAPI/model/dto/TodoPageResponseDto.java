package com.bryan.TodoListAPI.model.dto;

import java.util.List;

public record TodoPageResponseDto(
        List<TodoResponseDto> data,
        int page,
        int limit,
        int total
) {
}
