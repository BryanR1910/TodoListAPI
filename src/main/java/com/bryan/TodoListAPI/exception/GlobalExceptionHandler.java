package com.bryan.TodoListAPI.exception;

import com.bryan.TodoListAPI.model.payload.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handlerMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest webRequest){

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(e -> {
            String key = ((FieldError) e).getField();
            String value = e.getDefaultMessage();
            errors.put(key,value);
        });

        ApiResponse apiResponse = new ApiResponse(errors.toString(), webRequest.getDescription(false));
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handlerEmailAlreadyExists(EmailAlreadyExistsException ex, WebRequest webRequest){
        ApiResponse apiResponse = new ApiResponse(ex.getMessage(), webRequest.getDescription(false));

        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse> handlerInvalidCredentials(InvalidCredentialsException ex, WebRequest webRequest){
        ApiResponse apiResponse = new ApiResponse(ex.getMessage(), webRequest.getDescription(false));

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

    @ExceptionHandler(TodoNotFoundException.class)
    public ResponseEntity<ApiResponse> handlerTodoNotFound(TodoNotFoundException ex, WebRequest webRequest){
        ApiResponse apiResponse = new ApiResponse(ex.getMessage(), webRequest.getDescription(false));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse> handlerAuthentication(AuthenticationException ex, WebRequest webRequest){
        ApiResponse apiResponse = new ApiResponse(ex.getMessage(), webRequest.getDescription(false));

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }
}
