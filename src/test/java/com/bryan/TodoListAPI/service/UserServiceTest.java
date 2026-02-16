package com.bryan.TodoListAPI.service;

import com.bryan.TodoListAPI.exception.EmailAlreadyExistsException;
import com.bryan.TodoListAPI.model.User;
import com.bryan.TodoListAPI.model.dto.RegisterRequestDto;
import com.bryan.TodoListAPI.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    public void testCreateFromRequest(){
        //Given
        RegisterRequestDto userDto = new RegisterRequestDto("bryan", "bryan@gmail.com", "pass");
        //When
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepo.add(any(User.class))).thenReturn(1L);
        when(userRepo.findById(anyLong())).thenReturn(
                Optional.of(new User(1L ,"bryan", "bryan@gmail.com", "encodedPassword"))
        );


        User savedUser = userService.createFromRequest(userDto);
        //Then
        assertNotNull(savedUser);
        assertEquals("bryan@gmail.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());

        verify(this.userRepo).findByEmail(userDto.email());
        verify(this.userRepo).add(any(User.class));
        verify(this.userRepo).findById(1L);
    }

    @Test
    void testCreateFromRequestEmailAlreadyExists() {
        RegisterRequestDto dto =
                new RegisterRequestDto("bryan", "bryan@gmail.com", "pass");

        when(userRepo.findByEmail(dto.email()))
                .thenReturn(Optional.of(new User()));

        assertThrows(EmailAlreadyExistsException.class,
                () -> userService.createFromRequest(dto));

        verify(userRepo).findByEmail(dto.email());
        verify(userRepo, never()).add(any());
        verify(userRepo, never()).findById(anyLong());
    }

    @Test
    void testCreateFromRequestUserNotFoundAfterSave() {
        RegisterRequestDto dto =
                new RegisterRequestDto("bryan", "bryan@gmail.com", "pass");

        when(userRepo.findByEmail(dto.email()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encodedPassword");

        when(userRepo.add(any(User.class)))
                .thenReturn(1L);

        when(userRepo.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.createFromRequest(dto));

        verify(userRepo).add(any(User.class));
        verify(userRepo).findById(1L);
    }

    @Test
    public void testFindByEmail(){
        //Given
        String email = "bryan@gmail.com";
        //When
        when(userRepo.findByEmail(anyString())).thenReturn(
                Optional.of(new User("bryan", "bryan@gmail.com", "ppp"))
        );
        User user = userService.findByEmail(email);
        //Then
        assertNotNull(user);
        assertEquals("bryan@gmail.com", user.getEmail());

        verify(this.userRepo).findByEmail(anyString());
    }

    @Test
    void testFindByEmailNotFound() {
        String email = "missing@gmail.com";

        when(userRepo.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.findByEmail(email));

        verify(userRepo).findByEmail(email);
    }


    @Test
    public void testFindById(){
        //Given
        Long id = 1L;
        //When
        when(userRepo.findById(anyLong())).thenReturn(
                Optional.of(new User(1L ,"bryan", "bryan@gmail.com", "ppp"))
        );
        User user = userService.findById(id);
        //Then
        assertNotNull(user);
        assertEquals("bryan@gmail.com", user.getEmail());
        assertEquals("bryan", user.getName());

        verify(this.userRepo).findById(anyLong());
    }

    @Test
    void testFindByIdNotFound() {
        Long id = 99L;

        when(userRepo.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.findById(id));

        verify(userRepo).findById(id);
    }

}
