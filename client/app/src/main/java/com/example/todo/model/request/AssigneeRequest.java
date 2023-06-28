package com.example.todo.model.request;

public class AssigneeRequest {
    private Long task;
    private Long assignee;

    public AssigneeRequest() {

    }

    public AssigneeRequest(Long task, Long assignee) {
        this.task = task;
        this.assignee = assignee;
    }

    public Long getTask() {
        return task;
    }

    public void setTask(Long task) {
        this.task = task;
    }

    public Long getAssignee() {
        return assignee;
    }

    public void setAssignee(Long assignee) {
        this.assignee = assignee;
    }
}
