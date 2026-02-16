package com.bryan.TodoListAPI.service;

import com.bryan.TodoListAPI.exception.EmailAlreadyExistsException;
import com.bryan.TodoListAPI.model.User;
import com.bryan.TodoListAPI.model.dto.RegisterRequestDto;
import com.bryan.TodoListAPI.repository.UserRepo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public User createFromRequest(RegisterRequestDto requestDto){

        if(userRepo.findByEmail(requestDto.email()).isPresent()){
            throw new EmailAlreadyExistsException("Email already registered");
        }

        User newUser = new User(requestDto.name(), requestDto.email(),  passwordEncoder.encode(requestDto.password()));

        Long id = userRepo.add(newUser);
        return userRepo.findById(id).orElseThrow(() -> new UsernameNotFoundException(
                "User not found with id: " + id
        ));
    }

    public User findByEmail(String email){
        return userRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
                "User not found with email:" + email
        ));
    }

    public User findById(Long id){
        return userRepo.findById(id).orElseThrow(() -> new UsernameNotFoundException(
                "User not found with id:" + id
        ));
    }
}
