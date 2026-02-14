package com.bryan.TodoListAPI.controller;

import com.bryan.TodoListAPI.model.dto.TodoPageResponseDto;
import com.bryan.TodoListAPI.model.dto.TodoRequestDto;
import com.bryan.TodoListAPI.model.dto.TodoResponseDto;
import com.bryan.TodoListAPI.model.dto.TodoUpdateDto;
import com.bryan.TodoListAPI.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping("todos")
    public ResponseEntity<TodoResponseDto> addTodo(@Valid @RequestBody TodoRequestDto todoRequestDto, Authentication authentication){
        String email =authentication.getName();

        TodoResponseDto todoResponseDto = todoService.addTodo(todoRequestDto, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(todoResponseDto);
    }

    @PutMapping("todos/{id}")
    public ResponseEntity<TodoResponseDto> updateTodo(@RequestBody TodoUpdateDto updateDto, @PathVariable("id") Long todoId, Authentication authentication){
        String email = authentication.getName();

        TodoResponseDto todoResponseDto = todoService.updateTodo(updateDto,todoId ,email);
        return ResponseEntity.ok(todoResponseDto);
    }

    @DeleteMapping("todos/{id}")
    public ResponseEntity<Void> deletetodo(@PathVariable("id") Long todoId, Authentication authentication){
        String email = authentication.getName();

        todoService.deleteTodo(todoId, email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("todos")
    public ResponseEntity<TodoPageResponseDto> getTodos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "asc") String direction,
            Authentication authentication
    ){
        String email = authentication.getName();
        TodoPageResponseDto responseDto = todoService.getTodos(page, limit, email, sortBy, direction, title, description);

        return ResponseEntity.ok(responseDto);
    }




}
