package com.bryan.TodoListAPI.service;

import com.bryan.TodoListAPI.exception.TodoNotFoundException;
import com.bryan.TodoListAPI.model.Todo;
import com.bryan.TodoListAPI.model.User;
import com.bryan.TodoListAPI.model.dto.TodoPageResponseDto;
import com.bryan.TodoListAPI.model.dto.TodoRequestDto;
import com.bryan.TodoListAPI.model.dto.TodoResponseDto;
import com.bryan.TodoListAPI.model.dto.TodoUpdateDto;
import com.bryan.TodoListAPI.repository.TodoRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {

    private final TodoRepo todoRepo;
    private final UserService userService;


    public TodoService(TodoRepo todoRepo, UserService userService) {
        this.todoRepo = todoRepo;
        this.userService = userService;
    }

    public TodoResponseDto addTodo(TodoRequestDto todoDto, String email){
        User user = userService.findByEmail(email);

        Todo todo = new Todo(todoDto.title(),todoDto.description(), user.getId());
        Long id = todoRepo.add(todo);
        Todo newTodo = todoRepo.findById(id, user.getId()).orElseThrow(() -> new TodoNotFoundException("Todo not found with id:" + id));
        return new TodoResponseDto(id, newTodo.getTitle(), newTodo.getDescription());
    }

    public TodoResponseDto updateTodo(TodoUpdateDto updateDto,Long todoId, String email) {
        User user = userService.findByEmail(email);
        Todo todo = todoRepo.findById(todoId, user.getId()).orElseThrow(() -> new TodoNotFoundException("Todo not found with id: " + todoId));

        todo.setDescription(updateDto.description() != null ? updateDto.description() : todo.getDescription());
        todo.setTitle(updateDto.title() != null ? updateDto.title() : todo.getTitle());

        todoRepo.update(todo);
            return new TodoResponseDto(todoId, todo.getTitle(), todo.getDescription());
    }

    public void deleteTodo(Long todoId, String email) {
        User user = userService.findByEmail(email);
        Todo todo = todoRepo.findById(todoId, user.getId()).orElseThrow(() -> new TodoNotFoundException("Todo not found with id: " + todoId));

        todoRepo.delete(todoId, user.getId());
    }

    public TodoPageResponseDto getTodos(int page, int limit, String email, String sortBy, String direction, String title, String description) {
        User user = userService.findByEmail(email);
        int offset = (page - 1) * limit;

        if(!sortBy.equals("id") && !sortBy.equals("title")){
            sortBy = "id";
        }

        if(!direction.equals("asc") && !direction.equals("desc")){
            direction="asc";
        }

        List<Todo> todos = todoRepo.findAllByUserIdPaginated(title, description, sortBy, direction, limit, offset, user.getId());
        int total = todos.size();

        List<TodoResponseDto> todoResponseDtos = todos.stream().map(
                t -> new TodoResponseDto(t.getId(),t.getTitle(),t.getDescription())
        ).toList();

        return new TodoPageResponseDto(todoResponseDtos, page, limit, total);
    }
}

