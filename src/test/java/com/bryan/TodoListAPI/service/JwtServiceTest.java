package com.bryan.TodoListAPI.service;

import static org.junit.jupiter.api.Assertions.*;

import com.bryan.TodoListAPI.model.User;
import com.bryan.TodoListAPI.model.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.List;

public class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    public void init(){
        this.jwtService = new JwtService();

        String base64Key = Base64.getEncoder()
                .encodeToString("my-super-secret-key-that-is-long-enough-for-hs384".getBytes());

        ReflectionTestUtils.setField(jwtService, "secretKey", base64Key);
    }

    @Test
    void testGenerateTokenAndExtractEmail(){
        String email = "bryan@gmail.com";

        String token = jwtService.getToken(email);
        String extractedEmail = jwtService.getUserNameFromToken(token);

        assertEquals(email, extractedEmail);
    }

    @Test
    void testValidateToken(){
        String token = jwtService.getToken("bryan@gmail.com");
        User user = new User(1L, "bryan", "bryan@gmail.com", "encoded");
        UserDetails userDetails = new UserPrincipal(user);

        boolean isTokenValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isTokenValid);
    }

    @Test
    void testReturnFalseWhenUsernameDoesNotMatch() {
        String token = jwtService.getToken("bryan@gmail.com");

        User user = new User(1L, "bryan", "other@gmail.com", "encoded");
        UserDetails userDetails = new UserPrincipal(user);

        boolean isTokenValid = jwtService.isTokenValid(token, userDetails);
        assertFalse(isTokenValid);
    }
}
