package com.example.todo.models.request;

import com.example.todo.common.validator.CombineValidator;
import com.example.todo.common.validator.StringValidator;
import lombok.Data;

@Data
public class ConfirmRequest {
    private String password;

    public void validate() {
        new CombineValidator()
                .add(new StringValidator("password", this.getPassword()).empty())
                .check();
    }
}
