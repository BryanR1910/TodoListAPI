package com.bryan.TodoListAPI.mapper;

import com.bryan.TodoListAPI.model.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<User> {


    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setEmail(rs.getString("email"));
        user.setName(rs.getString("name"));
        user.setId(rs.getLong("id"));
        user.setPassword(rs.getString("password"));

        return user;
    }
}
