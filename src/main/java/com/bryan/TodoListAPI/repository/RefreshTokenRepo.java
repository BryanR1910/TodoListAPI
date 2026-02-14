package com.bryan.TodoListAPI.repository;

import com.bryan.TodoListAPI.mapper.RefreshTokenMapper;
import com.bryan.TodoListAPI.model.RefreshToken;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RefreshTokenRepo {
    private final JdbcTemplate jdbcTemplate;

    private final String SQL_INSERT ="insert into refresh_tokens (token, expiration, revoked, user_id) values (?,?,?,?)";
    private final String SQL_FIND = "select * from refresh_tokens where token = ?";
    private final String SQL_REVOKED = "update refresh_tokens set revoked = true where token = ?";


    public RefreshTokenRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void add(RefreshToken rt){
        jdbcTemplate.update(
                SQL_INSERT,
                rt.getToken(),
                rt.getExpiration(),
                rt.getRevoked(),
                rt.getUserId()
        );
    }

    public Optional<RefreshToken> findByToken(String token){
        try{
            return Optional.ofNullable(jdbcTemplate.queryForObject(SQL_FIND, new RefreshTokenMapper(), token) );
        } catch (EmptyResultDataAccessException ex){
            return Optional.empty();
        }
    }

    public void revoke(String token){
        jdbcTemplate.update(SQL_REVOKED, token);
    }



}
