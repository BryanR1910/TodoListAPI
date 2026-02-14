package com.bryan.TodoListAPI.repository;

import com.bryan.TodoListAPI.exception.EmailAlreadyExistsException;
import com.bryan.TodoListAPI.mapper.UserMapper;
import com.bryan.TodoListAPI.model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepo {

    private final JdbcTemplate jdbcTemplate;

    private final String SQL_ADD_USER = "INSERT INTO users(name, email, password) VALUES (?,?,?) RETURNING id";
    private final String SQL_FIND_BY_EMAIL="SELECT * FROM users WHERE email = ?";
    private final String SQL_FIND_BY_ID="SELECT * FROM users WHERE id = ?";

    public UserRepo(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long add(User user){
        try{
            return jdbcTemplate.queryForObject(SQL_ADD_USER, Long.class ,user.getName(), user.getEmail(), user.getPassword());
        } catch (DataIntegrityViolationException ex){
            throw new EmailAlreadyExistsException("Email already registered");
        }
    }

    public Optional<User> findById(Long id) {

        try{
            return Optional.ofNullable(jdbcTemplate.queryForObject(SQL_FIND_BY_ID, new UserMapper(), id));
        } catch (EmptyResultDataAccessException ex){
            return Optional.empty();
        }
    }

    public Optional<User> findByEmail(String email) {

        try{
            return Optional.ofNullable(jdbcTemplate.queryForObject(SQL_FIND_BY_EMAIL, new UserMapper(), email));
        } catch (EmptyResultDataAccessException ex){
            return Optional.empty();
        }
    }
}
