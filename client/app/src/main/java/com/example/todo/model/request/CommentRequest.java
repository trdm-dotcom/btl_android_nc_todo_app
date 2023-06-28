package com.example.todo.model.request;

public class CommentRequest {
    private String content;
    private Long task;

    public CommentRequest() {
    }

    public CommentRequest(String content, Long task) {
        this.content = content;
        this.task = task;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTask() {
        return task;
    }

    public void setTask(Long task) {
        this.task = task;
    }
}
