package com.example.todo.models.request;

import com.example.todo.common.validator.CombineValidator;
import com.example.todo.common.validator.StringValidator;
import lombok.Data;

@Data
public class LoginRequest {
    private String clientSecret;
    private String email;
    private String password;
    public void validate() {
        new CombineValidator()
                .add(new StringValidator("clientSecret", this.getClientSecret()).empty())
                .add(new StringValidator("email", this.getEmail()).empty())
                .add(new StringValidator("password", this.getPassword()).empty())
                .check();
    }
}
