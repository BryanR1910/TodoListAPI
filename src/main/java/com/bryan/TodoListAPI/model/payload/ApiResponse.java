package com.bryan.TodoListAPI.model.payload;

import java.time.LocalDate;

public class ApiResponse {
    private LocalDate time;
    private String message;
    private String url;

    public ApiResponse(String message, String url) {
        this.time = LocalDate.now();
        this.message = message;
        this.url = url.replace("uri=","");
    }

    public LocalDate getTime() {
        return time;
    }

    public void setTime(LocalDate time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
