package com.example.todo.model.request;

public class UserRequest {
    private String name;

    public UserRequest() {
    }

    public UserRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
