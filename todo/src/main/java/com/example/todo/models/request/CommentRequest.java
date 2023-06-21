package com.example.todo.models.request;

import com.example.todo.common.validator.CombineValidator;
import com.example.todo.common.validator.NumberValidator;
import com.example.todo.common.validator.StringValidator;
import lombok.Data;

@Data
public class CommentRequest {
    private String content;
    private Long task;

    public void validate(){
        new CombineValidator()
                .add(new NumberValidator("task", this.getTask()).notEmpty())
                .add(new StringValidator("content", this.getContent()).empty())
                .check();
    }
}
