package com.example.todo.models.request;

import com.example.todo.common.validator.CombineValidator;
import com.example.todo.common.validator.NumberValidator;
import lombok.Data;

import java.util.List;

@Data
public class AssigneeRequest {
    private Long task;
    private List<Long> assignee;

    public void validate() {
        new CombineValidator()
                .add(new NumberValidator("task", this.getTask()).notEmpty())
                .check();
    }
}
