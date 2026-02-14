package com.bryan.TodoListAPI.service;

import com.bryan.TodoListAPI.model.User;
import com.bryan.TodoListAPI.model.UserPrincipal;
import com.bryan.TodoListAPI.repository.UserRepo;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    public MyUserDetailsService(UserRepo userRepo){
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
                "User not found with email: " + email
        ));
        return new UserPrincipal(user);
    }
}
