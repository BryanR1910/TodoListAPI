package com.bryan.TodoListAPI.service;

import com.bryan.TodoListAPI.exception.TodoNotFoundException;
import com.bryan.TodoListAPI.model.Todo;
import com.bryan.TodoListAPI.model.User;
import com.bryan.TodoListAPI.model.dto.TodoPageResponseDto;
import com.bryan.TodoListAPI.model.dto.TodoRequestDto;
import com.bryan.TodoListAPI.model.dto.TodoResponseDto;
import com.bryan.TodoListAPI.model.dto.TodoUpdateDto;
import com.bryan.TodoListAPI.repository.TodoRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {
    @Mock
    private TodoRepo todoRepo;

    @Mock
    private UserService userService;

    @InjectMocks
    private TodoService todoService;

    @Test
    void testAddTodo() {
        //Given
        TodoRequestDto dto = new TodoRequestDto("todo", "test");
        String email = "bryan@gmail.com";

        //When
        when(userService.findByEmail(anyString())).thenReturn(
                new User(1L, "bryan", "bryan@gmail.com", "encodedPassword")
        );
        when(todoRepo.add(any(Todo.class))).thenReturn(1L);
        when(todoRepo.findById(1L, 1L)).thenReturn(
                Optional.of(new Todo(1L, dto.title(), dto.description(), 1L))
        );
        TodoResponseDto savedTodo = todoService.addTodo(dto, email);

        //Then
        assertEquals(savedTodo.description(), dto.description());
        assertEquals(savedTodo.title(), dto.title());
        assertEquals(savedTodo.id(), 1L);

        verify(userService).findByEmail(email);
        verify(todoRepo).add(any(Todo.class));
        verify(todoRepo).findById(anyLong(), anyLong());
    }

    @Test
    void testAddTodoThrowExceptionWhenTodoNotFound() {
        //Given
        TodoRequestDto dto = new TodoRequestDto("todo", "test");
        String email = "bryan@gmail.com";

        //When
        when(userService.findByEmail(anyString())).thenReturn(
                new User(1L, "bryan", "bryan@gmail.com", "encodedPassword")
        );
        when(todoRepo.add(any(Todo.class))).thenReturn(1L);
        when(todoRepo.findById(1L, 1L)).thenReturn(
                Optional.empty()
        );
        //Then
        assertThrows(TodoNotFoundException.class, () -> todoService.addTodo(dto, email));

        verify(userService).findByEmail(email);
        verify(todoRepo).add(any(Todo.class));
        verify(todoRepo).findById(anyLong(), anyLong());
    }

    @Test
    void testUpdateTodo(){
        //Given
        String email = "bryan@gmail.com";
        Long todoId = 1L;
        TodoUpdateDto dto = new TodoUpdateDto("updatedTitle","updatedDescription");

        Todo existingTodo = new Todo(todoId, "actualTitle", "actualDescription", 1L);

        //When
        when(userService.findByEmail(anyString())).thenReturn(
                new User(1L, "bryan", "bryan@gmail.com","encodedPassword")
        );
        when(todoRepo.findById(anyLong(), anyLong())).thenReturn(
                Optional.of(existingTodo)
        );
        TodoResponseDto response = todoService.updateTodo(dto,todoId,email);

        //Then
        assertNotNull(response);
        assertEquals(response.id(), todoId);
        assertEquals(response.title(), dto.title());
        assertEquals(response.description(), dto.description());

        verify(userService).findByEmail(email);
        verify(todoRepo).findById(1L, 1L);
        verify(todoRepo).update(existingTodo);
    }

    @Test
    void testUpdateTodoThrowExceptionWhenTodoNotFound(){
        //Given
        String email = "bryan@gmail.com";
        Long todoId = 1L;
        TodoUpdateDto dto = new TodoUpdateDto("updatedTitle","updatedDescription");

        Todo existingTodo = new Todo(todoId, "actualTitle", "actualDescription", 1L);

        //When
        when(userService.findByEmail(anyString())).thenReturn(
                new User(1L, "bryan", "bryan@gmail.com","encodedPassword")
        );
        when(todoRepo.findById(anyLong(), anyLong())).thenReturn(
                Optional.empty()
        );

        //Then
        assertThrows(TodoNotFoundException.class, () -> todoService.updateTodo(dto,todoId,email));

        verify(userService).findByEmail(email);
        verify(todoRepo, never()).update(any(Todo.class));
    }

    @Test
    void testUpdateTodoOnlyDescription(){
        //Given
        String email = "bryan@gmail.com";
        Long todoId = 1L;
        TodoUpdateDto dto = new TodoUpdateDto(null,"updatedDescription");

        Todo existingTodo = new Todo(todoId, "actualTitle", "actualDescription", 1L);

        //When
        when(userService.findByEmail(anyString())).thenReturn(
                new User(1L, "bryan", "bryan@gmail.com","encodedPassword")
        );
        when(todoRepo.findById(anyLong(), anyLong())).thenReturn(
                Optional.of(existingTodo)
        );
        TodoResponseDto response = todoService.updateTodo(dto, todoId, email);

        //Then
        assertEquals(response.description(), dto.description());
        assertEquals(response.title(), "actualTitle");

        verify(userService).findByEmail(email);
        verify(todoRepo).findById(todoId,1L);
        verify(todoRepo).update(any(Todo.class));
    }

    @Test
    void testUpdateTodoOnlyTitle(){
        //Given
        String email = "bryan@gmail.com";
        Long todoId = 1L;
        TodoUpdateDto dto = new TodoUpdateDto("updatedTitle",null);

        Todo existingTodo = new Todo(todoId, "actualTitle", "actualDescription", 1L);

        //When
        when(userService.findByEmail(anyString())).thenReturn(
                new User(1L, "bryan", "bryan@gmail.com","encodedPassword")
        );
        when(todoRepo.findById(anyLong(), anyLong())).thenReturn(
                Optional.of(existingTodo)
        );
        TodoResponseDto response = todoService.updateTodo(dto, todoId, email);

        //Then
        assertEquals(response.description(), "actualDescription");
        assertEquals(response.title(), dto.title());

        verify(userService).findByEmail(email);
        verify(todoRepo).findById(todoId,1L);
        verify(todoRepo).update(any(Todo.class));
    }

    @Test
    void testDeleteTodo(){
        //Given
        Long todoId = 1L;
        String email = "bryan@gmail.com";

        //When
        when(userService.findByEmail(anyString())).thenReturn(
                new User(1L, "bryan", "bryan@gmail.com", "encodedPassword")
        );
        when(todoRepo.findById(anyLong(), anyLong())).thenReturn(
                Optional.of(new Todo(1L, "title", "description", 1L))
        );

        todoService.deleteTodo(todoId, email);

        //Then
        verify(userService).findByEmail(email);
        verify(todoRepo).findById(todoId, 1L);
        verify(todoRepo).delete(todoId, 1L);
    }

    @Test
    void testDeleteTodoThrowExceptionWhenTodoNotFound(){
        //Given
        Long todoId = 1L;
        String email = "bryan@gmail.com";

        //When
        when(userService.findByEmail(anyString())).thenReturn(
                new User(1L, "bryan", "bryan@gmail.com", "encodedPassword")
        );
        when(todoRepo.findById(anyLong(), anyLong())).thenReturn(
                Optional.empty()
        );

        assertThrows(TodoNotFoundException.class, () -> todoService.deleteTodo(todoId, email));

        verify(userService).findByEmail(email);
        verify(todoRepo).findById(todoId, 1L);
        verify(todoRepo, never()).delete(todoId, 1L);
    }

    @Test
    void testGetTodos(){
        //Given
        int page = 1;
        int limit = 5;
        String email = "bryan@gmail.com";
        String sortBy = "id";
        String direction = "asc";

        User user = new User(1L, "bryan", "bryan@gmail.com", "encoded");
        List<Todo> mockTodos = List.of(
                new Todo(1L, "1t", "1d", 1L),
                new Todo(2L, "2t", "2d", 1L)
        );

        //When
        when(userService.findByEmail(anyString())).thenReturn(user);
        when(todoRepo.findAllByUserIdPaginated(
                any(), any(), anyString(), anyString(), anyInt(), anyInt(), anyLong()
        )).thenReturn(
                mockTodos
        );
        TodoPageResponseDto response = todoService.getTodos(page, limit, email, sortBy, direction, null, null);

        //Then
        assertEquals(page, response.page());
        assertEquals(limit, response.limit());
        assertEquals(2, response.total());
        assertEquals(2, response.data().size());
        assertEquals("1t", response.data().getFirst().title());

        int expectedOffset = (page - 1) * limit;

        verify(userService).findByEmail(email);
        verify(todoRepo).findAllByUserIdPaginated(null, null, sortBy, direction, limit, expectedOffset, user.getId());
    }

    @Test
    void testGetTodosDefaultSortByToIdWhenInvalid(){
        User user = new User(1L, "bryan", "bryan@gmail.com", "encoded");

        when(userService.findByEmail(anyString())).thenReturn(user);
        when(todoRepo.findAllByUserIdPaginated(any(),any(),anyString(), anyString(), anyInt(), anyInt(), anyLong())).thenReturn(
                List.of()
        );
        todoService.getTodos(1,10,"bryan@gmail.com", "InvalidField", "desc", null, null);


        verify(todoRepo).findAllByUserIdPaginated(any(), any(), eq("id"), eq("desc"), anyInt() ,anyInt(), eq(user.getId()));
    }

    @Test
    void testGetTodosDefaultDirectionToAscWhenInvalid(){
        User user = new User(1L, "bryan", "bryan@gmail.com", "encoded");

        when(userService.findByEmail(anyString())).thenReturn(user);
        when(todoRepo.findAllByUserIdPaginated(any(),any(),anyString(), anyString(), anyInt(), anyInt(), anyLong())).thenReturn(
                List.of()
        );
        todoService.getTodos(1,10,"bryan@gmail.com", "title", "InvalidField", null, null);


        verify(todoRepo).findAllByUserIdPaginated(any(), any(), eq("title"), eq("asc"), anyInt() ,anyInt(), eq(user.getId()));
    }


}
