package com.example.todo.models.request;

import com.example.todo.common.validator.CombineValidator;
import com.example.todo.common.validator.NumberValidator;
import lombok.Data;

@Data
public class AssigneeRequest {
    private Long task;
    private Long assignee;

    public void validate() {
        new CombineValidator()
                .add(new NumberValidator("task", this.getTask()).notEmpty())
                .add(new NumberValidator("assignee", this.getAssignee()).notEmpty())
                .check();
    }
}
