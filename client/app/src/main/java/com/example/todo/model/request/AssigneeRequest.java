package com.example.todo.model.request;

import java.util.List;

public class AssigneeRequest {
    private Long task;
    private List<Long> assignee;

    public AssigneeRequest() {
    }

    public AssigneeRequest(Long task, List<Long> assignee) {
        this.task = task;
        this.assignee = assignee;
    }

    public Long getTask() {
        return task;
    }

    public void setTask(Long task) {
        this.task = task;
    }

    public List<Long> getAssignee() {
        return assignee;
    }

    public void setAssignee(List<Long> assignee) {
        this.assignee = assignee;
    }
}
