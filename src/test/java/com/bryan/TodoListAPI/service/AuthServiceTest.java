package com.bryan.TodoListAPI.service;

import com.bryan.TodoListAPI.exception.AuthenticationException;
import com.bryan.TodoListAPI.exception.InvalidCredentialsException;
import com.bryan.TodoListAPI.model.RefreshToken;
import com.bryan.TodoListAPI.model.User;
import com.bryan.TodoListAPI.model.dto.AuthResponseDto;
import com.bryan.TodoListAPI.model.dto.LoginRequestDto;
import com.bryan.TodoListAPI.model.dto.RegisterRequestDto;
import com.bryan.TodoListAPI.repository.RefreshTokenRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.sql.Ref;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenRepo refreshTokenRepo;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_validRequest_returnsAuthResponse(){
        //Given
        RegisterRequestDto registerDto = new RegisterRequestDto("bryan", "bryan@gmail.com","pass");
        User newUser = new User(1L, "bryan", "bryan@gmail.com", "encoded");

        //When
        when(userService.createFromRequest(any(RegisterRequestDto.class))).thenReturn(newUser);
        when(jwtService.getToken(anyString())).thenReturn("Token");

        AuthResponseDto result = authService.register(registerDto);

        //Then
        assertEquals(result.accessToken(), "Token");

        verify(userService).createFromRequest(registerDto);
        verify(jwtService).getToken("bryan@gmail.com");

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepo).add(captor.capture());
        assertEquals(1L, captor.getValue().getUserId());
        assertTrue(captor.getValue().getExpiration().isBefore(LocalDateTime.now().plusDays(8)));
        assertFalse(captor.getValue().getRevoked());
        assertNotNull(captor.getValue().getToken());
    }

    @Test
    void login_validCredentials_returnsTokens(){
        //Given
        LoginRequestDto loginDto = new LoginRequestDto("bryan@gmail.com", "pass");
        User savedUser = new User(1L, "bryan", "bryan@gmail.com", "encoded");

        //When
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userService.findByEmail(anyString())).thenReturn(savedUser);
        when(jwtService.getToken(anyString())).thenReturn("token");


        AuthResponseDto response = authService.login(loginDto);

        //Then
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepo).add(captor.capture());
        assertEquals(1L, captor.getValue().getUserId());
        assertTrue(captor.getValue().getExpiration().isBefore(LocalDateTime.now().plusDays(8)));
        assertFalse(captor.getValue().getRevoked());

        verify(userService).findByEmail(loginDto.email());
        verify(jwtService).getToken(savedUser.getEmail());
        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
        assertEquals("token", response.accessToken());
    }

    @Test
    void login_invalidCredentials_throwsInvalidCredentialsException(){
        LoginRequestDto loginDto = new LoginRequestDto("bryan@gmail.com", "pass");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(InvalidCredentialsException.class, () -> authService.login(loginDto));
    }

    @Test
    void refresh_validToken_returnsNewTokens(){
        String refreshTokenValue = "refresh-token";
        RefreshToken rt = new RefreshToken(1L,refreshTokenValue, LocalDateTime.now().plusDays(7), false, 2L);
        User user = new User(2L, "bryan", "bryan@gmail.com", "encoded");

        when(refreshTokenRepo.findByToken(anyString())).thenReturn(
                Optional.of(rt)
        );
        when(userService.findById(anyLong())).thenReturn(user);
        when(jwtService.getToken(anyString())).thenReturn("access-token");

        AuthResponseDto response = authService.refresh(refreshTokenValue);

        // new refresh token
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepo).add(captor.capture());
        assertEquals(2L, captor.getValue().getUserId());
        assertTrue(captor.getValue().getExpiration().isAfter(LocalDateTime.now()));
        assertFalse(captor.getValue().getRevoked());

        verify(userService).findById(user.getId());
        verify(jwtService).getToken(user.getEmail());
        verify(refreshTokenRepo).revoke(refreshTokenValue);
    }

    @Test
    void refresh_tokenNotFound_throwsAuthenticationException(){
        String refreshTokenValue = "refresh-token";

        when(refreshTokenRepo.findByToken(anyString())).thenReturn(
                Optional.empty()
        );
        assertThrows(AuthenticationException.class, () -> authService.refresh(refreshTokenValue));
    }

    @Test
    void refresh_tokenRevoked_throwsAuthenticationException(){
        String refreshTokenValue = "refresh-token-revoked";
        RefreshToken rt = new RefreshToken(1L,refreshTokenValue, LocalDateTime.now().plusDays(7), true, 2L);

        when(refreshTokenRepo.findByToken(anyString())).thenReturn(
                Optional.of(rt)
        );

        assertThrows(AuthenticationException.class, () -> authService.refresh(refreshTokenValue));
    }

    @Test
    void refresh_tokenExpired_throwsAuthenticationException(){
        String refreshTokenValue = "refresh-token-expired";
        RefreshToken rt = new RefreshToken(1L,refreshTokenValue, LocalDateTime.now().minusMinutes(1), false, 2L);

        when(refreshTokenRepo.findByToken(anyString())).thenReturn(
                Optional.of(rt)
        );

        assertThrows(AuthenticationException.class, () -> authService.refresh(refreshTokenValue));
    }
}
