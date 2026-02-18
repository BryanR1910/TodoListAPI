package com.bryan.TodoListAPI.service;

import com.bryan.TodoListAPI.exception.AuthenticationException;
import com.bryan.TodoListAPI.exception.InvalidCredentialsException;
import com.bryan.TodoListAPI.model.RefreshToken;
import com.bryan.TodoListAPI.model.User;
import com.bryan.TodoListAPI.model.dto.AuthResponseDto;
import com.bryan.TodoListAPI.model.dto.LoginRequestDto;
import com.bryan.TodoListAPI.model.dto.RegisterRequestDto;
import com.bryan.TodoListAPI.repository.RefreshTokenRepo;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenRepo refreshTokenRepo;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserService userService, JwtService jwtService, RefreshTokenRepo refreshTokenRepo, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenRepo = refreshTokenRepo;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponseDto register(RegisterRequestDto requestDto){
        User newUser = userService.createFromRequest(requestDto);

        String refreshTokenValue= generateRefreshToken();
        String accessToken = jwtService.getToken(newUser.getEmail());

        RefreshToken rt = new RefreshToken();
        rt.setUserId(newUser.getId());
        rt.setExpiration(LocalDateTime.now().plusDays(7));
        rt.setToken(refreshTokenValue);
        rt.setRevoked(false);
        refreshTokenRepo.add(rt);

        return new AuthResponseDto(accessToken, refreshTokenValue);
    }

    public AuthResponseDto login(LoginRequestDto loginDto){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password()));
        } catch (BadCredentialsException ex){
            throw new InvalidCredentialsException("Invalid email or password");
        }
        User user = userService.findByEmail(loginDto.email());

        String refreshTokenValue= generateRefreshToken();
        String accessToken = jwtService.getToken(user.getEmail());

        RefreshToken rt = new RefreshToken();
        rt.setUserId(user.getId());
        rt.setExpiration(LocalDateTime.now().plusDays(7));
        rt.setToken(refreshTokenValue);
        rt.setRevoked(false);
        refreshTokenRepo.add(rt);

        AuthResponseDto authResponseDto = new AuthResponseDto(accessToken, refreshTokenValue);
        return authResponseDto;
    }

    public AuthResponseDto refresh(String refreshTokenValue) {

        RefreshToken storedToken = refreshTokenRepo.findByToken(refreshTokenValue).orElseThrow(
                () -> new AuthenticationException("Invalid refresh token")
        );
        if(storedToken.getRevoked()){
            throw new AuthenticationException("Refresh token revoked");
        }

        if(storedToken.getExpiration().isBefore(LocalDateTime.now())){
            throw new AuthenticationException("Refresh token expired");
        }
        User user = userService.findById(storedToken.getUserId());

        refreshTokenRepo.revoke(refreshTokenValue);

        String newRefreshTokenValue = generateRefreshToken();
        String newAccessTokenValue = jwtService.getToken(user.getEmail());
        RefreshToken rt = new RefreshToken();
        rt.setToken(newRefreshTokenValue);
        rt.setExpiration(LocalDateTime.now().plusDays(7));
        rt.setRevoked(false);
        rt.setUserId(user.getId());
        refreshTokenRepo.add(rt);

        return new AuthResponseDto(newAccessTokenValue, newRefreshTokenValue);
    }

    private String generateRefreshToken(){
        return UUID.randomUUID().toString();
    }
}
