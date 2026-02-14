package com.bryan.TodoListAPI.model;

import java.time.LocalDateTime;

public class RefreshToken {
    private Long id;
    private String token;
    private LocalDateTime expiration;
    private Boolean revoked;
    private Long userId;

    public RefreshToken(){
    }

    public RefreshToken(Long id, String token, LocalDateTime expiration, Boolean revoked, Long userId) {
        this.id = id;
        this.token = token;
        this.expiration = expiration;
        this.revoked = revoked;
        this.userId = userId;
    }

    public RefreshToken(String token, LocalDateTime expiration, Boolean revoked, Long userId) {
        this.token = token;
        this.expiration = expiration;
        this.revoked = revoked;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiration() {
        return expiration;
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }

    public Boolean getRevoked() {
        return revoked;
    }

    public void setRevoked(Boolean revoked) {
        this.revoked = revoked;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
