package com.bryan.TodoListAPI.service;

import com.bryan.TodoListAPI.model.User;
import com.bryan.TodoListAPI.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class MyUserDetailsServiceTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private MyUserDetailsService myUserDetailsService;

    @Test
    void LoadUserByUsername(){
        String email = "bryan@gmail.com";
        User user = new User(1L, "bryan", "bryan@gmail.com", "encoded");

        when(userRepo.findByEmail(anyString())).thenReturn(
                Optional.of(user)
        );
        UserDetails result = myUserDetailsService.loadUserByUsername(email);

        assertNotNull(result);
        assertEquals(result.getUsername(), user.getEmail());
        assertEquals(result.getPassword(), user.getPassword());

        verify(userRepo).findByEmail(email);
    }

    @Test
    void LoadUserByUsernameThrowExceptionWhenUserNotFound(){
        String email = "bryan@gmail.com";
        User user = new User(1L, "bryan", "bryan@gmail.com", "encoded");

        when(userRepo.findByEmail(anyString())).thenReturn(
                Optional.empty()
        );

        assertThrows(UsernameNotFoundException.class, () -> myUserDetailsService.loadUserByUsername(email));

        verify(userRepo).findByEmail(email);
    }
}
