package com.bryan.TodoListAPI.repository;

import com.bryan.TodoListAPI.mapper.TodoMapper;
import com.bryan.TodoListAPI.model.Todo;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TodoRepo {
    private final JdbcTemplate jdbcTemplate;

    private final String SQL_CREATE_TODO="insert into todos (title,description,user_id) values (?,?,?) RETURNING id";
    private final String SQL_FIND_BY_ID="select * from todos where id = ? and user_id= ?";
    private final String SQL_DELETE_TODO="delete from todos where id = ? and user_id = ?";
    private final String SQL_UPDATE_TODO="update todos set title = ?, description = ? where id = ? AND user_id = ?";

    public TodoRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long add(Todo todo){
        return jdbcTemplate.queryForObject(SQL_CREATE_TODO, Long.class, todo.getTitle(), todo.getDescription(), todo.getUserId());
    }

    public Optional<Todo> findById(Long id, Long userId){
        try{
            return Optional.ofNullable(jdbcTemplate.queryForObject(SQL_FIND_BY_ID, new TodoMapper(), id, userId));
        } catch (EmptyResultDataAccessException ex){
            return Optional.empty();
        }
    }

    public List<Todo> findAllByUserIdPaginated(String title, String description, String sortBy, String direction, int limit, int offset, Long userId){

        StringBuilder sql = new StringBuilder(
                "SELECT * FROM todos WHERE user_id = ? "
        );

        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (title != null && !title.isBlank()) {
            sql.append("AND title ILIKE ? ");
            params.add("%" + title + "%");
        }

        if (description != null && !description.isBlank()) {
            sql.append("AND description ILIKE ? ");
            params.add('%' + description + '%');
        }

        sql.append("ORDER BY ").append(sortBy)
                .append(" ").append(direction)
                .append(" LIMIT ? OFFSET ?");

        params.add(limit);
        params.add(offset);

        return jdbcTemplate.query(
                sql.toString(),
                new TodoMapper(),
                params.toArray()
        );
    }

    public boolean delete(Long todoId, Long userId){
        return jdbcTemplate.update(SQL_DELETE_TODO, todoId, userId) > 0;
    }

    public boolean update(Todo todo){
        return jdbcTemplate.update(SQL_UPDATE_TODO, todo.getTitle(), todo.getDescription(), todo.getId(), todo.getUserId()) > 0;
    }
}
