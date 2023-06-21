package com.example.todo.models.request;

import com.example.todo.common.validator.CombineValidator;
import com.example.todo.common.validator.StringValidator;
import lombok.Data;

@Data
public class RegisterRequest {
    private String password;
    private String name;
    private String email;
    public void validate() {
        new CombineValidator()
                .add(new StringValidator("name", this.getName()).empty())
                .add(new StringValidator("password", this.getPassword()).empty())
                .add(new StringValidator("email", this.getEmail()).empty())
                .check();
    }
}
