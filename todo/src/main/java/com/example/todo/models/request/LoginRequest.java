package com.example.todo.models.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String clientSecret;
    private String username;
    private String password;
}
