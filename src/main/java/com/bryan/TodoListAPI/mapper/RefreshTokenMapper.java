package com.bryan.TodoListAPI.mapper;

import com.bryan.TodoListAPI.model.RefreshToken;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RefreshTokenMapper implements RowMapper<RefreshToken> {
    @Override
    public RefreshToken mapRow(ResultSet rs, int rowNum) throws SQLException {
        RefreshToken rt = new RefreshToken();
        rt.setId(rs.getLong("id"));
        rt.setToken(rs.getString("token"));
        rt.setExpiration(rs.getTimestamp("expiration").toLocalDateTime());
        rt.setRevoked(rs.getBoolean("revoked"));
        rt.setUserId(rs.getLong("user_id"));
        return rt;
    }
}
