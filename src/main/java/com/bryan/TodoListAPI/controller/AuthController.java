package com.bryan.TodoListAPI.controller;

import com.bryan.TodoListAPI.model.dto.AuthResponseDto;
import com.bryan.TodoListAPI.model.dto.LoginRequestDto;
import com.bryan.TodoListAPI.model.dto.RefreshRequestDto;
import com.bryan.TodoListAPI.model.dto.RegisterRequestDto;
import com.bryan.TodoListAPI.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody RegisterRequestDto registerDto){
        AuthResponseDto authResponseDto = authService.register(registerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponseDto);
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto loginDto){
        AuthResponseDto authResponseDto = authService.login(loginDto);
        return ResponseEntity.ok(authResponseDto);
    }

    @PostMapping("refresh")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody RefreshRequestDto refreshDto){
        AuthResponseDto authResponseDto = authService.refresh(refreshDto.refreshToken());

        return ResponseEntity.ok(authResponseDto);
    }

}
